import { useNavigate, useParams } from "react-router-dom";

import TaskForm from "../components/TaskForm";
import { useTaskEditPresenter } from "../presenters/useTaskEditPresenter";

function TaskEditPage() {

    const { id } = useParams();
    const navigate = useNavigate();

    const {
        title,
        status,
        errorMessage,
        isLoading,
        handleTitleChange,
        handleStatusChange,
        handleUpdateTask
    } = useTaskEditPresenter(id);

    const handleSubmit = async (event) => {

        const updatedTask = await handleUpdateTask(event);

        if (updatedTask) {
            navigate("/");
        }
    };

    const handleCancel = () => {
        navigate("/");
    };

    if (isLoading) {
        return <p>読み込み中...</p>;
    }

    return (
        <div>
            <h1>タスク編集</h1>

            <TaskForm
                mode="edit"
                title={title}
                status={status}
                errorMessage={errorMessage}
                onTitleChange={handleTitleChange}
                onStatusChange={handleStatusChange}
                onSubmit={handleSubmit}
                onCancel={handleCancel}
            />
        </div>
    );
}

export default TaskEditPage;