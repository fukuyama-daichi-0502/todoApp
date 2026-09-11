import TaskForm from "../components/TaskForm";
import { useTaskCreatePresenter } from "../presenters/useTaskCreatePresenter";
import { Link, useNavigate } from "react-router-dom";

function TaskCreatePage() {

    const navigate = useNavigate();

    const {
        title,
        errorMessage,
        handleTitleChange,
        handleCreateTask
    } = useTaskCreatePresenter();

    const handleSubmit = async (event) => {
        const createdTask = await handleCreateTask(event);

        if(createdTask){
            navigate("/");
        }
    }

    return (
        <div>
            <h1>新規タスク作成</h1>

            <TaskForm
                mode="create"
                title={title}
                errorMessage={errorMessage}
                onTitleChange={handleTitleChange}
                onSubmit={handleSubmit}
            />

            <Link to="/">
                一覧へ戻る
            </Link>

        </div>
    );
}

export default TaskCreatePage;