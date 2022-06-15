//package nl.bioinf.premonition.util;
//
//import nl.bioinf.premonition.services.FileUploadConstraint;
//
//public class FileUploadValidator implements
//        ConstraintValidator<FileUploadConstraint, String> {
//
//    @Override
//    public void initialize(FileUploadConstraint contactNumber) {
//    }
//
//    @Override
//    public boolean isValid(String contactField,
//                           ConstraintValidatorContext cxt) {
//        return contactField != null && contactField.matches("[0-9]+")
//                && (contactField.length() > 8) && (contactField.length() < 14);
//    }
//
//}