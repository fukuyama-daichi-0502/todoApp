import { Link } from "react-router-dom";
import TaskDetail from "../components/TaskDetail";
import { useTaskDetailPresenter } from "../presenters/useTaskDetailPresenter";

function TaskDetailPage() {
    const {
        task,
        description,

        isLoading,
        isSaving,

        loadErrorMessage,
        saveErrorMessage,
        saveMessage,

        handleDescriptionChange,
        handleDescriptionSave
    } = useTaskDetailPresenter();

    if (isLoading) {
        <p>読み込み中...</p>;
    }

    if (loadErrorMessage) {
        return(<p>{loadErrorMessage}</p>);
    }

    if (task == null) {
        return(<p>タスクがありません</p>);
    }

    return (
        <div>
            <h1>タスク詳細</h1>

            <TaskDetail task = {task} description = {description} isSaving = {isSaving} saveErrorMessage = {saveErrorMessage} saveMessage = {saveMessage} onDescriptionChange = {handleDescriptionChange} onDescriptionSave = {handleDescriptionSave}/>
            <div>
                <Link to = {`/tasks/${task.id}/edit`}>編集</Link>
            </div>
            <div>
                <Link to = "/">タスク一覧へ戻る</Link>
            </div>
            
        </div>
    );
}

export default TaskDetailPage;