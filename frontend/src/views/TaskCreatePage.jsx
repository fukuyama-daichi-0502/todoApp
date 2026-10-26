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
        recurrenceRule,
        recurrenceBaseDay,
        errorMessage,
        handleTitleChange,
        handleDescription,
        handleDueDateChange,
        handlePriorityChange,
        handleCategoryChange,
        handleRecurrenceRuleChange,
        handleRecurrenceBaseDayChange,
        handleCreateTask
    } = useTaskCreatePresenter();

    const handleSubmit = async (event) => {
        const createdTask = await handleCreateTask(event);

        if(createdTask){
            navigate("/");
        }
    }
    
    const handleCancel = () => {
        navigate("/");
    };

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
                recurrenceRule={recurrenceRule}
                recurrenceBaseDay={recurrenceBaseDay}
                errorMessage={errorMessage}
                onTitleChange={handleTitleChange}
                onDescriptionChange={handleDescription}
                onDueDateChange={handleDueDateChange}
                onPriorityChange={handlePriorityChange}
                onCategoryChange={handleCategoryChange}
                onRecurrenceRuleChange={handleRecurrenceRuleChange}
                onRecurrenceBaseDayChange={handleRecurrenceBaseDayChange}
                onSubmit={handleSubmit}
                onCancel={handleCancel}
            />

            <Link to="/">
                一覧へ戻る
            </Link>

        </div>
    );
}

export default TaskCreatePage;