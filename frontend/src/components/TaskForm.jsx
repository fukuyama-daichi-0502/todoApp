import "./TaskForm.css";
import { useState } from "react";

function TaskForm({
    mode,
    isSubtask = false,
    title,
    description,
    status,
    dueDate,
    priority,
    categoryId,
    categories,
    recurrenceRule,
    recurrenceBaseDay,
    onRecurrenceRuleChange,
    onRecurrenceBaseDayChange,
    errorMessage,
    onTitleChange,
    onStatusChange,
    onDueDateChange,
    onPriorityChange,
    onCategoryChange,
    onDescriptionChange,
    onSubmit,
    onCancel
}){

    
    const[isRecurrenceDayPickerOpen, setIsRecurrenceDayPickerOpen] = useState(false);

    return(
        <form onSubmit = {onSubmit}>

            <table className = "formTable" >
                <tbody>
                    <tr>
                        <th>タイトル</th>
                        <td>
                            <input type = "text" value = {title} onChange = {onTitleChange} />
                        </td>
                    </tr>

                    {mode === "edit" &&(
                        <tr>
                            <th>タスク詳細</th>
                            <td>
                                <textarea className = "task-description-input" value = {description} onChange = {onDescriptionChange} maxLength = {2000} rows = {8}/>
                                <div className = "description-count">{description.length}/2000文字</div>
                            </td>                            
                        </tr>
                    )}

                    {mode === "edit" &&(
                        <tr>
                            <th>ステータス</th>
                            <td>
                                <select value = {status} onChange = {onStatusChange}>
                                    <option value = "TODO">未着手</option>
                                    <option value = "IN_PROGRESS">進行中</option>
                                    <option value = "ON_HOLD">保留</option>
                                    <option value = "DONE">完了</option>
                                    <option value = "CANCELLED">中止</option>
                                </select>
                            </td>
                        </tr>
                    )}

                    <tr>
                        <th>期限</th>
                        <td>
                            <input type = "date" value = {dueDate} onChange = {onDueDateChange} />
                        </td>
                    </tr>
                    
                    {!isSubtask && (
                        <>
                            <tr>
                                <th>繰り返し</th>
                                <td>
                                    <select value = {recurrenceRule} onChange = {(event) => {setIsRecurrenceDayPickerOpen(false); onRecurrenceRuleChange(event)}}>
                                        <option value = "NONE">なし</option>
                                        <option value = "DAILY">毎日</option>
                                        <option value = "WEEKLY">毎週</option>
                                        <option value = "MONTHLY">毎月</option>
                                    </select>
                                </td>
                            </tr>

                            {recurrenceRule === "MONTHLY" &&(
                                <tr>
                                    <th>基準日</th>
                                    <td>
                                        <div className = "recurrence-day-picker">
                                            <button type = "button" className = "recurrence-day-picker-button" onClick = {() => setIsRecurrenceDayPickerOpen(!isRecurrenceDayPickerOpen)}>
                                                {Number(recurrenceBaseDay) === 31 ? "31日（月末）" : recurrenceBaseDay !== "" ? `${recurrenceBaseDay}日` : "基準日を選択"}
                                            </button>

                                            {isRecurrenceDayPickerOpen && (
                                                <div className = "recurrence-day-panel">
                                                    {Array.from({length: 31},(_, index) => {
                                                        const day = index + 1;

                                                        return(
                                                            <button key ={day} type = "button"
                                                                className = {Number(recurrenceBaseDay) === day ? "recurrence-day selected" : "recurrence-day"}
                                                                onClick = {() => {onRecurrenceBaseDayChange(day); setIsRecurrenceDayPickerOpen(false);}}>
                                                                    {day}
                                                            </button>
                                                        );
                                                    })}

                                                    <button type = "button"
                                                        className = {Number(recurrenceBaseDay) === 31 ? "recurrence-end-of-month selected" : "recurrence-end-of-month"}
                                                        onClick = {() => {onRecurrenceBaseDayChange(31); setIsRecurrenceDayPickerOpen(false);}}>
                                                            月末
                                                    </button>
                                                </div>
                                            )}
                                        </div>
                                    </td>
                                </tr>
                            )}
                        </>
                    )}

                    <tr>
                        <th>優先度</th>
                        <td>
                            <select value = {priority} onChange = {onPriorityChange}>
                                <option value = "">未設定</option>
                                <option value = "LOW">低</option>
                                <option value = "MEDIUM">中</option>
                                <option value = "HIGH">高</option>
                            </select>
                        </td>
                    </tr>
                        

                    <tr>
                        <th>カテゴリ</th>
                        <td>
                            <select value = {categoryId} onChange = {onCategoryChange}>
                                <option value = "">カテゴリなし</option>

                                {categories.map((category) => (
                                    <option key = {category.id} value = {category.id}>
                                        {category.name}
                                    </option>
                                ))}
                            </select>
                        </td>
                    </tr>
                </tbody>
            </table>

            {errorMessage && (
                <p>{errorMessage}</p>
            )}
            
            <div className = "formButton">
                <button>
                    保存
                </button>

                <button type = "button" onClick={onCancel}>
                    キャンセル
                </button>
            </div>

        </form>
    );
}

export default TaskForm;