package com.smesitejl.product.validation;

import com.smesitejl.product.entity.Product;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class ProductValidator implements ConstraintValidator<ValidProduct , Product> {

    @Override
    public boolean isValid(Product product, ConstraintValidatorContext context) {

        context.disableDefaultConstraintViolation();

        if(product == null){
            context.buildConstraintViolationWithTemplate("Product cannot be null")
                    .addConstraintViolation();
            return false;
        }

        //TITLE
        String title = product.getTitle();

        if(title == null) {
            context.buildConstraintViolationWithTemplate("Product title cannot be null")
                    .addConstraintViolation();
            return false;
        }

        if(title.trim().isEmpty()){
            context.buildConstraintViolationWithTemplate("Product title cannot be empty")
                    .addConstraintViolation();
            return false;
        }

        if(title.length() > 255){
            context.buildConstraintViolationWithTemplate("Product title is longer than 255 characters")
                    .addConstraintViolation();
            return false;
        }

        //DESCRIPTION
        String description = product.getDescription();

        if(description == null){
                context.buildConstraintViolationWithTemplate("Product description cannot be null")
                        .addConstraintViolation();
        return false;
        }

        if(description.trim().isEmpty()){
            context.buildConstraintViolationWithTemplate("Product description cannot be empty")
                    .addConstraintViolation();
            return false;
        }

        if(description.length() > 1000){
            context.buildConstraintViolationWithTemplate("Product description is longer than 1000 characters")
                    .addConstraintViolation();
            return false;
        }

        //PHOTO URL
        String photoUrl = product.getPhotoUrl();
        if(photoUrl == null){
            context.buildConstraintViolationWithTemplate("Product photoUrl cannot be null")
                    .addConstraintViolation();
            return false;
        }

        if(photoUrl.trim().isEmpty()){
            context.buildConstraintViolationWithTemplate("Product photoUrl cannot be empty")
                    .addConstraintViolation();
            return false;
        }

        if(photoUrl.length() > 2048){
            context.buildConstraintViolationWithTemplate("Product photoUrl is longer than 2048 characters")
                    .addConstraintViolation();
            return false;
        }

        if(photoUrl.matches("^(https?|ftp)://[^\\s/$.?#].\\S*$")){
            context.buildConstraintViolationWithTemplate("Product photoUrl is not valid")
                    .addConstraintViolation();
            return false;
        }

        //PRICE
        BigDecimal price = product.getPrice();

        if(price == null){
            context.buildConstraintViolationWithTemplate("Product price cannot be null")
                    .addConstraintViolation();
            return false;
        }

        if(price.compareTo(BigDecimal.ZERO) < 0 || price.compareTo(BigDecimal.ZERO) == 0){
            context.buildConstraintViolationWithTemplate("Price must be greater than 0")
                    .addConstraintViolation();
            return false;
        }

        if(price.scale() != 2){
            context.buildConstraintViolationWithTemplate("Price must be a valid decimal number with up to two decimal places")
                    .addConstraintViolation();
            return false;
        }

        if(price.precision() > 10){
            context.buildConstraintViolationWithTemplate("Price cannot be longer than 10 characters")
                    .addConstraintViolation();
            return false;
        }

        //CATEGORY
        String category = product.getCategory();

        if(category == null){
            context.buildConstraintViolationWithTemplate("Product category cannot be null")
                    .addConstraintViolation();
            return false;
        }
        if(category.trim().isEmpty()){
            context.buildConstraintViolationWithTemplate("Product category cannot be empty")
                    .addConstraintViolation();
            return false;
        }
        if(category.length() > 255){
            context.buildConstraintViolationWithTemplate("Product category is longer than 255 characters")
                    .addConstraintViolation();
            return false;
        }

        //QUANTITY
        Integer quantity = product.getQuantity();
        if(quantity == null){
            context.buildConstraintViolationWithTemplate("Product quantity cannot be null")
                    .addConstraintViolation();
            return false;
        }
        if(quantity < 0){
            context.buildConstraintViolationWithTemplate("Product quantity cannot be negative")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
