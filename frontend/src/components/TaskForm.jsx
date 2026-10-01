import "./TaskForm.css";

function TaskForm({
    mode,
    title,
    description,
    status,
    dueDate,
    priority,
    categoryId,
    categories,
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
                                <dev className = "description-count">{description.length}/2000文字</dev>
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

                    <tr>
                        <th>優先度</th>
                        <td>
                            <select value = {priority} onChange = {onPriorityChange}>
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

                    {errorMessage && (
                        <p>{errorMessage}</p>
                    )}

                    
                </tbody>
            </table>
            
            <div className = "formButton">
                <button>
                    保存
                </button>

                {mode === "edit" &&(
                    <button type = "button" onClick={onCancel}>
                        キャンセル
                    </button>
                )}
            </div>

        </form>
    );
}

export default TaskForm;