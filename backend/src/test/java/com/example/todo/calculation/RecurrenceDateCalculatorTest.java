package com.example.todo.calculation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.todo.entity.RecurrenceRule;

public class RecurrenceDateCalculatorTest {
    private RecurrenceDateCalculator recurrenceDateCalculator;

    @BeforeEach 
    void setUp(){
        recurrenceDateCalculator = new RecurrenceDateCalculator();
    }

    //繰り返し設定毎日の場合
    @Test 
    void calculateNextDueDateReturnsNextDayForDaily(){
        //準備：テストデータの作成
        LocalDate currentDueDate = LocalDate.of(2026, 10, 23);

        //実行：作成したデータで次のタスクの期限を計算
        LocalDate nextDueDate = recurrenceDateCalculator.calculateNextDueDate(currentDueDate, RecurrenceRule.DAILY, null); 
    
        //確認：期待した結果になっているか
        assertEquals(LocalDate.of(2026, 10, 24), nextDueDate);
    }

    //繰り返し設定毎週の場合
    @Test 
    void calculateNextDueDateRetunsNextWeekForWeekly(){
        //準備：テストデータの作成
        LocalDate currentDueDate = LocalDate.of(2026, 10, 23);

        //実行：作成したデータで次のタスクの期限を計算
        LocalDate nextDueDate = recurrenceDateCalculator.calculateNextDueDate(currentDueDate, RecurrenceRule.WEEKLY, null);
    
        //確認：期待した結果になっているか
        assertEquals(LocalDate.of(2026, 10, 30), nextDueDate);
    }

    //繰り返し設定毎月（月末以外）の場合
    @Test 
    void calculateNextDueDateReturnsSameDayNextMonthForMonthly(){
        //準備：テストデータの作成
        LocalDate currentDueDate = LocalDate.of(2026, 10, 15);
        Long recurrenceBaseDay = 15L;

        //実行：作成したデータで次のタスクの期限を計算
        LocalDate nextDueDate = recurrenceDateCalculator.calculateNextDueDate(currentDueDate, RecurrenceRule.MONTHLY, recurrenceBaseDay);

        //確認：期待した結果になっているか
        assertEquals(LocalDate.of(2026, 11, 15), nextDueDate);
    }

    //繰り返し設定毎月（基準日が次の月末にない）場合
    @Test 
    void calculateNextDueDateUsesLastDayWhenBaseDayDoesNotExist(){
        //準備：テストデータの作成
        LocalDate currentDueDate = LocalDate.of(2026, 1, 31);
        Long recurrenceBaseDay = 31L;

        //実行：作成したデータで次のタスクの期限を計算
        LocalDate nextDueDate = recurrenceDateCalculator.calculateNextDueDate(currentDueDate, RecurrenceRule.MONTHLY,recurrenceBaseDay);

        //確認：期待した結果になっているか
        assertEquals(LocalDate.of(2026, 2, 28), nextDueDate);
    }

    //繰り返し設定毎月（基準日よりも短い月の次のタスク）の場合
    @Test
    void calculateNextDueDateReturnsTo31AfterShortMonth(){
        //準備：テストデータの作成
        LocalDate currentDueDate = LocalDate.of(2026, 2, 28);
        Long recurrenceBaseDay = 31L;

        //実行：作成したデータで次のタスクの期限を計算
        LocalDate nextDueDate = recurrenceDateCalculator.calculateNextDueDate(currentDueDate, RecurrenceRule.MONTHLY, recurrenceBaseDay);

        //確認：期待した結果になっているか
        assertEquals(LocalDate.of(2026, 3, 31), nextDueDate);
    }

    //繰り返し設定毎月（うるう年）の場合
    @Test 
    void calculateNextDueDateUsesFebruary29InLeapYear(){
        //準備：テストデータの作成
        LocalDate currentDueDate = LocalDate.of(2028, 1, 31);
        Long recurrenceBaseDay = 31L;

        //実行：作成したデータで次のタスクの期限を計算
        LocalDate nextDueDate = recurrenceDateCalculator.calculateNextDueDate(currentDueDate, RecurrenceRule.MONTHLY, recurrenceBaseDay);
        
        //確認：期待した結果になっているか
        assertEquals(LocalDate.of(2028, 2, 29), nextDueDate);
    }

}
