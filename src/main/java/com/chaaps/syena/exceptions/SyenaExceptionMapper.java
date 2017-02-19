package com.chaaps.syena.exceptions;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Provider
@Component
public class SyenaExceptionMapper implements ExceptionMapper<SyenaException> {
	Logger logger = Logger.getLogger(SyenaExceptionMapper.class);

	@Override
	public Response toResponse(SyenaException exception) {
		logger.debug("In toResponse " + getStackTrace(exception));

		return Response.status(Status.NOT_ACCEPTABLE).entity(exception.getLocalizedMessage()).build();
	}

	/**
	 * Creates and returns a {@link java.lang.String} from t’s stacktrace
	 * 
	 * @param t
	 *            Throwable whose stack trace is required
	 * @return String representing the stack trace of the exception
	 */
	public String getStackTrace(Throwable t) {
		StringWriter stringWritter = new StringWriter();
		PrintWriter printWritter = new PrintWriter(stringWritter, true);
		t.printStackTrace(printWritter);
		printWritter.flush();
		stringWritter.flush();
		printWritter.close();
		String trace = stringWritter.toString();
		try {
			stringWritter.close();
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return trace;
	}
}
