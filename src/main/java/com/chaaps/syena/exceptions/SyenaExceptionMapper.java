package com.chaaps.syena.exceptions;

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
		logger.debug("In toResponse " + exception.getLocalizedMessage());

		return Response.status(Status.NOT_ACCEPTABLE).entity(exception.getLocalizedMessage()).build();
	}

}
