package com.example.todo.calculation;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

import com.example.todo.entity.RecurrenceRule;
import com.example.todo.exception.InvalidRequestException;

public class RecurrenceDateCalculator {
    public LocalDate calculateNextDueDate(LocalDate currentDueDate, RecurrenceRule recurrenceRule, Long recurrenceBaseDay){
        if(currentDueDate == null){
            throw new InvalidRequestException("繰り返しタスクには期限が必要です");
        }

        if(recurrenceRule == null){
            throw new InvalidRequestException("繰り返し設定がありません");
        }

        return switch(recurrenceRule){
            case DAILY ->
                currentDueDate.plusDays(1);

            case WEEKLY ->
                currentDueDate.plusWeeks(1);

            case MONTHLY -> {
                if(recurrenceBaseDay == null || recurrenceBaseDay < 1 || recurrenceBaseDay > 31){
                    throw new InvalidRequestException("月次繰り返しの基準日が不正です");
                }

                YearMonth nextMonth = YearMonth.from(currentDueDate).plusMonths(1);

                int nextDay = Math.min(recurrenceBaseDay.intValue(), nextMonth.lengthOfMonth());

                yield nextMonth.atDay(nextDay);
            }

            case NONE ->
                throw new InvalidRequestException("繰り返し設定がありません");
        };
    }
    
    public LocalDate calculateNextSubtaskDueDate(LocalDate currentSubtaskDueDate, LocalDate currentParentDueDate, LocalDate nextParentDueDate){
        if(currentSubtaskDueDate == null){
            return null;
        }

        long dueDateDifference = ChronoUnit.DAYS.between(currentParentDueDate, nextParentDueDate);

        return currentSubtaskDueDate.plusDays(dueDateDifference);
    }
}
