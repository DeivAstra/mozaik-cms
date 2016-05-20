/**
 * This file is part of Mozaik CMS            www.mozaik.top
 * Copyright © 2016 Denis N Ivanchik       danykey@gmail.com
**/
package top.mozaik.frnd.site;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.ResultSetExtractor;

import top.mozaik.bknd.api.ServicesFacade;
import top.mozaik.bknd.api.service.QueryService;
import top.mozaik.frnd.site.init.DbInit;

public class ResourceServlet extends HttpServlet {

	private static final String query = "select r.data_content_type,rd.content_type,rd.file_name,rd.size,rd.data"
			+ " from wcm_resources r join wcm_resource_data rd"
			+ " on rd.id=r.data_id where rd.id=";

	private final QueryService queryService = ServicesFacade.$()
			.getQueryService();

	private static class ResourceExtractor implements ResultSetExtractor<ResourceData> {
		@Override
		public ResourceData extractData(ResultSet rs) throws SQLException, DataAccessException {
			if(!rs.next()) return null;
			
			String contentType;
			if ((contentType = rs.getString(1)) == null) {
				if ((contentType = rs.getString(2)) == null) {
					contentType = "application/octet-stream";
				}
			}
			return new ResourceData(
				contentType,
				rs.getString(3),
				rs.getInt(4),
				rs.getBinaryStream(5)
			);
		}
	};
	
	private static final ResourceExtractor resourceExtractor = new ResourceExtractor();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final OutputStream out = resp.getOutputStream();
		try {
			final HttpSession session = req.getSession();
			if(!DbInit.init(session)){
				return;
			}
			
			final Integer id = Integer.parseInt(req.getParameter("id"));
			final boolean download = "1".equals(req.getParameter("d"));
			final ResourceData data = queryService.readByQuery(query + id, resourceExtractor);
			if (data == null) {
				resp.setStatus(HttpStatus.NOT_FOUND.value());
				return;
			}
			resp.setContentType(data.contentType);
			//resp.setContentLength(data.size);
			resp.setHeader("Content-disposition", "attachment; filename=" + data.fileName);
			// write data stream
			final InputStream in = data.stream;
			int i;
			while ((i = in.read()) != -1) {
				out.write(i);
			}
			in.close();
		} catch (Throwable t) {
			t.printStackTrace();
			//throw new ServletException(t);
			//resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			//resp.setContentType("text/html");
			//t.printStackTrace(new PrintWriter(out));
		} finally {
			out.flush();
		}

	}

	private static class ResourceData {
		final String contentType;
		final String fileName;
		final int size;
		final InputStream stream;
		
		public ResourceData(String contentType, String fileName, int size, InputStream stream) {
			this.contentType = contentType;
			this.fileName = fileName;
			this.size = size;
			this.stream = stream;
		}
	}
}
