import "./TodoList.css";
import { formatDateTime } from "../formatters/dateTimeFormatter";
import { Link } from "react-router-dom";

function TodoList({ tasks, onDelete,onStatusChange  }) {

    const getTitleClassName = (status) => {

        if (status === "DONE") {
            return "task-title-done";
        }

        if (status === "CANCELLED") {
            return "task-title-cancelled";
        }

        return "";
        };

    const getRowClassName = (status) => {

        if (status === "DONE") {
            return "task-row-done";
        }

        return "";
    };

    return (
        <div>

            {tasks.length === 0 ? (
                <p>タスクはありません</p>
            ): (
                <table className = "taskTable">
                    <thead className = "tableHead">
                        <tr>
                            <th>ID</th>
                            <th>タイトル</th>
                            <th>ステータス</th>
                            <th>登録日</th>
                            <th>編集</th>
                            <th>削除</th>
                        </tr>

                    </thead>
                    <tbody>
                        {tasks.map((task) => (
                            <tr className = {getRowClassName(task.status)} key={task.id}>
                                <td>{task.id}</td>
                                <td  className={getTitleClassName(task.status)}>{task.title}</td>
                                <td>
                                    <select className = {`status status-${task.status.toLowerCase()}`} value = {task.status} onChange = {(event) => onStatusChange(task.id, event.target.value)}>
                                        <option value = "TODO">未着手</option>
                                        <option value = "IN_PROGRESS">進行中</option>
                                        <option value = "ON_HOLD">保留</option>
                                        <option value = "DONE">完了</option>
                                        <option value = "CANCELLED">中止</option>
                                    </select>

                                </td>
                                <td>{formatDateTime(task.createdAt)}</td>
                                <td>
                                    <Link className = "button" to = {`/tasks/${task.id}/edit`}>編集</Link>
                                </td>
                                <td>
                                    <button type = "button" onClick = {() => onDelete(task.id)}>削除</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                 </table>
            )}
        </div>
    );
}

export default TodoList;