import TaskForm from "../components/TaskForm";
import { useTaskCreatePresenter } from "../presenters/useTaskCreatePresenter";
import { Link, useNavigate } from "react-router-dom";

function TaskCreatePage() {

    const navigate = useNavigate();

    const {
        title,
        description,
        dueDate,
        priority,
        categoryId,
        categories,
        errorMessage,
        handleTitleChange,
        handleDescription,
        handleDueDateChange,
        handlePriorityChange,
        handleCategoryChange,
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
                description={description}
                dueDate={dueDate}
                priority={priority}
                categoryId={categoryId}
                categories={categories}
                errorMessage={errorMessage}
                onTitleChange={handleTitleChange}
                onDescriptionChange={handleDescription}
                onDueDateChange={handleDueDateChange}
                onPriorityChange={handlePriorityChange}
                onCategoryChange={handleCategoryChange}
                onSubmit={handleSubmit}
            />

            <Link to="/">
                一覧へ戻る
            </Link>

        </div>
    );
}

export default TaskCreatePage;