package main;

import com.helger.commons.error.IError;
import com.helger.schematron.ISchematronResource;
import com.helger.schematron.pure.SchematronResourcePure;
import com.helger.schematron.pure.errorhandler.IPSErrorHandler;
import com.helger.schematron.pure.errorhandler.LoggingPSErrorHandler;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        String schematronFilePath = "constraints.sch";
        System.out.println("ABC!!!!!");
        // Load the Schematron file
        SchematronResourcePure schematron = SchematronResourcePure.fromFile(new File(schematronFilePath));
        schematron.setErrorHandler(new LoggingPSErrorHandler());
        boolean valid = schematron.isValidSchematron();
        schematron.validateCompletely(aError -> handle(aError));
        System.out.println(valid);
    }

    private static void handle(IError error) {
        System.out.println(1);
        System.out.println(error);
        int x = 3;
    }
}
