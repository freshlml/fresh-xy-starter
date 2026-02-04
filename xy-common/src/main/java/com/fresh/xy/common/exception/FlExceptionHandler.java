package com.fresh.xy.common.exception;

import com.fresh.core.constants.FreshCommonConstants;
import com.fresh.core.enums.JsonResultEnum;
import com.fresh.core.exception.BizException;
import com.fresh.core.result.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@Slf4j
@ControllerAdvice
public class FlExceptionHandler {

    /**
     * Biz异常
     * @param ex BizException对象
     * @return JsonResult
     */
    @ExceptionHandler(BizException.class)
    @ResponseBody
    public JsonResult handlerBizException(BizException ex) {
        //ex.printStackTrace();
        log.error("业务异常: {}", ex.getMessage() , ex);
        return JsonResult.buildFailedResult(ex.getExceptionCodeWith(null), ex.getMessage());
    }


    /**
     * 参数校验异常
     * @param ex izException对象
     * @return JsonResult
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public JsonResult handlerBindException(BindException ex) {
        //ex.printStackTrace();
        log.error("参数校验异常: {}", ex.getMessage(), ex);
        return JsonResult.buildFailedResult(JsonResultEnum.FAIL.getCode(),
                                            ex.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public JsonResult handlerValidateException(MethodArgumentNotValidException  ex) {
        //ex.printStackTrace();
        log.error("参数校验异常: {}", ex.getMessage(), ex);
        return JsonResult.buildFailedResult(JsonResultEnum.FAIL.getCode(),
                                            ex.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public JsonResult handlerArgTypeException(MethodArgumentTypeMismatchException ex) {
        //ex.printStackTrace();
        log.error("参数校验异常: {}", ex.getMessage(), ex);
        return JsonResult.buildFailedResult(JsonResultEnum.FAIL.getCode(), FreshCommonConstants.ARG_TYPE_ERROR);
    }


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public JsonResult exceptionHandler(Exception ex) {
        //ex.printStackTrace();
        log.error("系统异常: {}", ex.getMessage(), ex);
        return JsonResult.buildFailedResult(JsonResultEnum.FAIL.getCode(), FreshCommonConstants.SYSTEM_ERROR);
    }

}
