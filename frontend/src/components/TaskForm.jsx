function TaskForm({mode,title,status,errorMessage,onTitleChange,onStatusChange,onSubmit,onCancel}){

    return(
        <form onSubmit = {onSubmit}>

            <div>
                <label>タイトル</label>

                <input type = "text" value = {title} onChange = {onTitleChange} />

            </div>

            {errorMessage && (
                <p>{errorMessage}</p>
            )}

            {mode === "edit" &&(
                <div>
                    <label htmlFor = "status">ステータス</label>

                    <select id ="status" value = {status} onChange = {onStatusChange}>
                        <option value = "TODO">未着手</option>
                        <option value = "IN_PROGRESS">進行中</option>
                        <option value = "ON_HOLD">保留</option>
                        <option value = "DONE">完了</option>
                        <option value = "CANCELLED">中止</option>
                    </select>
                </div>
            )}

            <button>
                保存
            </button>

            {mode === "edit" &&(
                <button type = "button" onClick={onCancel}>
                    キャンセル
                </button>
            )}

        </form>
    );
}

export default TaskForm;