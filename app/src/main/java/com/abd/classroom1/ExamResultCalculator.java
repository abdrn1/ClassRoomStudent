package com.abd.classroom1;


import java.util.List;

/**
 * Created by Abd on 3/16/2016.
 */
public class ExamResultCalculator {

    public static double CalculateExamResult(List<QuestionItem> questionsList) {
        double mcqResult = 0;
        double truefalseResult = 0;
        double fillResult = 0;
        double finalResult = 0;
        for (QuestionItem qi : questionsList) {
            switch (qi.getQuestionType()) {
                case QuestionItem.QMCQ:
                    mcqResult = mcqResult + calculateMCQ(qi);
                    break;
                case QuestionItem.QTRUEORFALSE:
                    truefalseResult = truefalseResult + calculateTrueFalse(qi);
                    break;
                case QuestionItem.MFILL:
                    fillResult = fillResult + calculateFill(qi);
                    break;
            }
        }
        finalResult = mcqResult + truefalseResult + fillResult;
        return finalResult;
    }

    private static int calculateMCQ(QuestionItem mcqItem) {
        int result = 0;
        List<QuestionItem.ChoiceItem> choiceList = mcqItem.getChoices();
        for (QuestionItem.ChoiceItem chi : choiceList) {
            if (chi.isChecked()) {
                if (chi.isStudentChecked()) {
                    result = mcqItem.getQuestionWeight();

                }

            }
        }

        return result;
    }

    private static int calculateTrueFalse(QuestionItem tfItem) {

        if (tfItem.getStudentQuestionAnswer().equals(tfItem.getQuestionAnswer())) {
            return tfItem.getQuestionWeight();
        }
        return 0;
    }

    private static int calculateFill(QuestionItem fillItem) {
        if (fillItem.getStudentQuestionAnswer().equals(fillItem.getQuestionAnswer())) {
            return fillItem.getQuestionWeight();
        }
        return 0;
    }

}
