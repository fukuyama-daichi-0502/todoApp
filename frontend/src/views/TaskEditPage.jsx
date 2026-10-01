import { useNavigate, useParams } from "react-router-dom";

import TaskForm from "../components/TaskForm";
import { useTaskEditPresenter } from "../presenters/useTaskEditPresenter";

function TaskEditPage() {

    const { id } = useParams();
    const navigate = useNavigate();

    const {
        title,
        description,
        status,
        dueDate,
        priority,
        categoryId,
        categories,
        errorMessage,
        isLoading,
        handleTitleChange,
        handleDescription,
        handleStatusChange,
        handleDueDateChange,
        handlePriorityChange,
        handleCategoryChange,
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
                description={description}
                status={status}
                dueDate={dueDate}
                priority={priority}
                categoryId={categoryId}
                categories={categories}
                errorMessage={errorMessage}
                onTitleChange={handleTitleChange}
                onDescriptionChange={handleDescription}
                onStatusChange={handleStatusChange}
                onDueDateChange={handleDueDateChange}
                onPriorityChange={handlePriorityChange}
                onCategoryChange={handleCategoryChange}
                onSubmit={handleSubmit}
                onCancel={handleCancel}
            />
        </div>
    );
}

export default TaskEditPage;