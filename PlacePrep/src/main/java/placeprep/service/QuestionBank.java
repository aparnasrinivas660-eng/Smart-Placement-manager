package placeprep.service;

import placeprep.model.AppData;
import placeprep.model.Question;

import java.util.List;

public final class QuestionBank {
    private QuestionBank() { }

    public static void seedIfEmpty(AppData data) {
        if (!data.getQuestions().isEmpty()) {
            return;
        }

        data.getQuestions().add(new Question("APT1", "Aptitude",
                "If 5 workers complete a job in 10 days, how many days will 10 workers take at the same rate?",
                List.of("2 days", "5 days", "10 days", "20 days"), 1));

        data.getQuestions().add(new Question("APT2", "Aptitude",
                "Find the simple interest on ₹1,000 at 5% per year for 2 years.",
                List.of("₹50", "₹75", "₹100", "₹150"), 2));

        data.getQuestions().add(new Question("APT3", "Aptitude",
                "What is the average of 10, 20 and 30?",
                List.of("15", "20", "25", "30"), 1));

        data.getQuestions().add(new Question("APT4", "Aptitude",
                "Two numbers are in the ratio 2:3 and their sum is 25. What are the numbers?",
                List.of("10 and 15", "12 and 13", "5 and 20", "8 and 17"), 0));

        data.getQuestions().add(new Question("APT5", "Aptitude",
                "A vehicle travels at 60 km/h for 2.5 hours. What distance does it cover?",
                List.of("120 km", "140 km", "150 km", "180 km"), 2));

        data.getQuestions().add(new Question("TECH1", "Technical",
                "Why is Java commonly described as platform independent?",
                List.of("It has no compiler", "Bytecode runs on a compatible JVM",
                        "It only runs in browsers", "It uses only machine code"), 1));

        data.getQuestions().add(new Question("TECH2", "Technical",
                "Which Java keyword is used when one class inherits another class?",
                List.of("extends", "implements", "inherits", "superclass"), 0));

        data.getQuestions().add(new Question("TECH3", "Technical",
                "What is the index of the first element in a Java array?",
                List.of("1", "0", "-1", "Depends on array size"), 1));

        data.getQuestions().add(new Question("TECH4", "Technical",
                "Which statement about Java String objects is correct?",
                List.of("Strings are immutable", "Strings are always mutable",
                        "Strings cannot be compared", "Strings store only numbers"), 0));

        data.getQuestions().add(new Question("TECH5", "Technical",
                "Which OOP concept allows the same method name to show different behaviour?",
                List.of("Encapsulation", "Abstraction", "Polymorphism", "Compilation"), 2));
    }
}
