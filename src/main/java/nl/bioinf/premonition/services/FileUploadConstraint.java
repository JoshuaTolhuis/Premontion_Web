//package nl.bioinf.premonition.services;
//
//import nl.bioinf.premonition.util.FileUploadValidator;
//
//import java.lang.annotation.*;
//
//@Documented
//@Constraint(validatedBy = FileUploadValidator.class)
//@Target( { ElementType.METHOD, ElementType.FIELD })
//@Retention(RetentionPolicy.RUNTIME)
//
//public interface FileUploadConstraint {
//        String message() default "Invalid File";
//        Class<?>[] groups() default {};
//        Class<? extends Payload>[] payload() default {};
//    }
