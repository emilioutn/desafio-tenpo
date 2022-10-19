package com.exampletenpo.calculate.error;

import com.exampletenpo.calculate.util.HeaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.violations.ConstraintViolationProblem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.rmi.ServerException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ExceptionTranslator implements ProblemHandling {
    private static final String FIELD_ERRORS_KEY = "fieldErrors";
    private static final String MESSAGE_KEY = "message";
    private static final String PATH_KEY = "path";
    private static final String VIOLATIONS_KEY = "violations";

    @Value("${application.clientApp.name}")
    private String applicationName;

    /**
     * Post-process the Problem payload to add the message key for the front-end if needed.
     */
    @Override
    public ResponseEntity<Problem> process(
            @Nullable ResponseEntity<Problem> entity, NativeWebRequest request) {
        if (entity == null) {
            return entity;
        }
        Problem problem = entity.getBody();
        if (!(problem instanceof ConstraintViolationProblem || problem instanceof DefaultProblem)) {
            return entity;
        }
        ProblemBuilder builder =
                Problem.builder()
                        .withType(null)
                        .withStatus(problem.getStatus())
                        .withTitle(problem.getTitle())
                        .with(PATH_KEY, request.getNativeRequest(HttpServletRequest.class).getRequestURI());

        if (problem instanceof ConstraintViolationProblem) {
            builder
                    .with(VIOLATIONS_KEY, ((ConstraintViolationProblem) problem).getViolations())
                    .with(MESSAGE_KEY, ErrorConstants.ERR_VALIDATION);
        } else {
            builder
                    .withCause(((DefaultProblem) problem).getCause())
                    .withDetail(problem.getDetail())
                    .withInstance(problem.getInstance());
            problem.getParameters().forEach(builder::with);
            if (!problem.getParameters().containsKey(MESSAGE_KEY) && problem.getStatus() != null) {
                builder.with(MESSAGE_KEY, "error.http." + problem.getStatus().getStatusCode());
            }
        }
        return new ResponseEntity<>(builder.build(), entity.getHeaders(), entity.getStatusCode());
    }

    @Override
    public ResponseEntity<Problem> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, @Nonnull NativeWebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<FieldErrorVM> fieldErrors =
                result.getFieldErrors().stream()
                        .map(
                                f ->
                                        new FieldErrorVM(
                                                f.getObjectName()
                                                        .replaceFirst("DTO$", "")
                                                        .replaceFirst("Dto$", ""),
                                                f.getField(), f.getCode()
                                        )
                        )
                        .collect(Collectors.toList());

        Problem problem =
                Problem.builder()
                        .withType(null)
                        .withTitle("Method argument not valid")
                        .withStatus(defaultConstraintViolationStatus())
                        .with(MESSAGE_KEY, ErrorConstants.ERR_VALIDATION)
                        .with(FIELD_ERRORS_KEY, fieldErrors)
                        .build();
        return create(ex, problem, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleBadRequestAlertException(
            BadRequestAlertException ex, NativeWebRequest request) {
        return create(
                ex,
                request,
                HeaderUtil.createFailureAlert(
                        applicationName, true, ex.getEntityName(), ex.getErrorKey(), ex.getMessage()));
    }

    //NOT_FOUND
    @ExceptionHandler
    public ResponseEntity<Problem> handleNoSuchElementException(
            NoSuchElementException ex, NativeWebRequest request) {
        Problem problem =
                Problem.builder()
                        .withTitle(String.valueOf(Status.NOT_FOUND))
                        .withDetail(ex.getMessage())
                        .withStatus(Status.NOT_FOUND)
                        .build();
        return create(ex, problem, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleIdentifyAlreadyExistException(
            IdentifyAlreadyExistException ex, NativeWebRequest request) {
        Problem problem =
                Problem.builder()
                        .withTitle(String.valueOf(Status.NOT_MODIFIED))
                        .withDetail(ex.getMessage())
                        .withStatus(Status.NOT_MODIFIED)
                        .build();
        return create(ex, problem, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleHttpClientErrorException(HttpClientErrorException ex,
                                                                  NativeWebRequest request) {
        Problem problem;
        if (ex.getRawStatusCode() == 403) {
            problem = Problem.builder()
                    .withTitle(String.valueOf(Status.FORBIDDEN))
                    .withDetail(ex.getLocalizedMessage().split("\"")[3])
                    .withStatus(Status.FORBIDDEN)
                    .build();
        } else if (ex.getRawStatusCode() == 401) {
            problem = Problem.builder()
                    .withTitle(String.valueOf(Status.UNAUTHORIZED))
                    .withDetail(ex.getLocalizedMessage().split("\"")[5])
                    .withStatus(Status.UNAUTHORIZED)
                    .build();
        } else {
            problem = Problem.builder()
                    .withTitle(String.valueOf(Status.NOT_FOUND))
                    .withDetail(ex.getLocalizedMessage().split("\"")[3])
                    .withStatus(Status.NOT_FOUND)
                    .build();
        }
        log.error("#########Exception occurred: " + problem.getTitle() + ": " + problem.getDetail());
        return create(ex, problem, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleUnauthorizedException(
            UnauthorizedException ex, NativeWebRequest request) {
        Problem problem =
                Problem.builder()
                        .withTitle(String.valueOf(Status.UNAUTHORIZED))
                        .withDetail(ex.getMessage())
                        .withStatus(Status.UNAUTHORIZED)
                        .build();
        return create(ex, problem, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleAccessDeniedException(
            AccessDeniedException ex, NativeWebRequest request) {
        Problem problem =
                Problem.builder()
                        .withTitle(String.valueOf(Status.FORBIDDEN))
                        .withDetail(ex.getMessage())
                        .withStatus(Status.FORBIDDEN)
                        .build();
        return create(ex, problem, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleInvalidRequestException(
            InvalidRequestException ex, NativeWebRequest request) {
        Problem problem =
                Problem.builder()
                        .withTitle(String.valueOf(Status.BAD_REQUEST))
                        .withDetail(ex.getMessage())
                        .withStatus(Status.BAD_REQUEST)
                        .build();
        return create(ex, problem, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleEntityNotFoundException(
            EntityNotFoundException ex, NativeWebRequest request) {
        Problem problem =
                Problem.builder()
                        .withTitle(String.valueOf(Status.NOT_FOUND))
                        .withDetail(ex.getMessage())
                        .withStatus(Status.NOT_FOUND)
                        .build();
        return create(ex, problem, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleServerException(
            ServerException ex, NativeWebRequest request) {
        Problem problem =
                Problem.builder()
                        .withTitle(String.valueOf(Status.SERVICE_UNAVAILABLE))
                        .withDetail(ex.getMessage())
                        .withStatus(Status.SERVICE_UNAVAILABLE)
                        .build();
        return create(ex, problem, request);
    }

}
