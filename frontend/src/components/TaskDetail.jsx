import "./TaskDetail.css"
import { formatDateTime, formatDate } from "../formatters/dateTimeFormatter";

function TaskDetail({
    task,
    description,

    isSaving,

    saveErrorMessage,
    saveMessage,

    onDescriptionChange,
    onDescriptionSave
}){
    return(
        <div>

            <table className = "task-detail-table">

                <tbody>
                    <tr>
                        <th>ID</th>
                        <td>{task.id}</td>
                    </tr>

                    <tr>
                        <th>タイトル</th>
                        <td>{task.title}</td>
                    </tr>

                    <tr>
                        <th>タスク詳細</th>
                        <td>
                            <textarea className = "task-description" value = {description} onChange = {onDescriptionChange} maxLength = {2000} />
                            <dev className = "description-count">{description.length}/2000文字</dev>
                        </td>
                    </tr>

                    <tr>
                        <th>ステータス</th>
                        <td>{task.statusLabel}</td>
                    </tr>

                    <tr>
                        <th>期限</th>
                        <td>{formatDate(task.dueDate) ?? "-"}</td>
                    </tr>

                    <tr>
                        <th>優先度</th>
                        <td>{task.priorityLabel}</td>
                    </tr>

                    <tr>
                        <th>カテゴリ</th>
                        <td>{task.categoryName ?? "カテゴリなし"}</td>
                    </tr>

                    <tr>
                        <th>登録日</th>
                        <td>{formatDateTime(task.createdAt)}</td>
                    </tr>

                    <tr>
                        <th>更新日</th>
                        <td>{formatDateTime(task.updatedAt)}</td>
                    </tr>

                </tbody>
            </table>

            {saveErrorMessage && (<p>{saveErrorMessage}</p>)}
            {saveMessage && (<p>{saveMessage}</p>)}

            <button type = "button" onClick = {onDescriptionSave} disabled = {isSaving}>
                {isSaving ? "保存中..." : "詳細を保存"}

            </button>
        </div>
    );
}

export default TaskDetail;