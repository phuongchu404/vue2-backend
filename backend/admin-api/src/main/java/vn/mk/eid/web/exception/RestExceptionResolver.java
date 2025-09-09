package vn.mk.eid.web.exception;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import vn.mk.eid.common.data.ResultCode;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.common.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static vn.mk.eid.common.data.ServiceResult.fail;

/**
 * rest exception resolver
 *
 * @author liukeshao
 * @date 2017/10/16
 */
@Slf4j
@Component
public class RestExceptionResolver extends AbstractHandlerExceptionResolver {

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 1;
	}

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception ex) {
		response.setContentType("application/json; charset=UTF-8");
		try (PrintWriter writer = response.getWriter()) {
			if (ex instanceof ServiceException) {
				response.setStatus(HttpStatus.OK.value());
				String msg = new Gson().toJson(fail((ServiceException) ex));
				writer.print(msg);
			} else if (ex instanceof MethodArgumentNotValidException) {
				response.setStatus(HttpStatus.OK.value());
				MethodArgumentNotValidException e = (MethodArgumentNotValidException) ex;
				String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
				String msg = new Gson().toJson(ServiceResult.fail(ResultCode.VALIDATE_ERROR.getCode(), errorMessage));
				writer.print(msg);
			} else if (ex instanceof IllegalArgumentException) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				writer.print(getMsg(ex.getMessage()));
			} else if (ex instanceof IllegalStateException) {
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
				writer.print(getMsg(ex.getMessage()));
			} else if (ex instanceof RestException) {
				response.setStatus(((RestException) ex).getStatus().value());
				writer.print(getMsg(ex.getMessage()));
			} else if (ex instanceof MissingServletRequestParameterException) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				writer.print(getMsg("parameters.invalid"));
			} else if (ex instanceof RestClientException) {
				String msg = new Gson().toJson(ServiceResult.fail(ResultCode.INVOKE_CORE_ERROR.getCode(), ResultCode.INVOKE_CORE_ERROR.getDescription() + ex.getMessage()));
				writer.print(msg);
			} else if (ex instanceof ResourceNotFoundException) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                String msg = new Gson().toJson(ServiceResult.fail(String.valueOf(HttpStatus.BAD_REQUEST.value()), ex.getMessage()));
                writer.print(msg);
            } else if (ex instanceof BadRequestException) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                String msg = new Gson().toJson(ServiceResult.fail(String.valueOf(HttpStatus.BAD_REQUEST.value()), ex.getMessage()));
                writer.print(msg);
            } else {
				log.error("Internal Server Error", ex);
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
				writer.print(getMsg("server.error"));
			}
		} catch (IOException e) {
			log.error("Handling of [{}] resulted in Exception", e.getClass().getName());
		}
		return new ModelAndView();
	}

	private String getMsg(String msg) {
		Map<String, Object> map = new HashMap<>();
		map.put("errMsg", msg);
		return new Gson().toJson(map);
	}
}
