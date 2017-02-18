package com.chaaps.syena.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

@Provider
public class SyenaGenericExceptionMapper implements ExceptionMapper<Throwable> {
Logger logger= Logger.getLogger(SyenaGenericExceptionMapper.class);

	@Override
	public Response toResponse(Throwable exception) {
		logger.debug("In toResponse " +exception.getLocalizedMessage());
		return Response.status(Status.NOT_FOUND).entity(exception.getLocalizedMessage()).build();
	}

}
