import { useNavigate, useParams } from "react-router-dom";
import TaskForm from "../components/TaskForm";
import { useSubtaskCreatePresenter } from "../presenters/useSubtaskCreatePresenter";

function SubtaskCreatePage(){
    const { parentId } = useParams();

    const {
        title,
        dueDate,
        priority,
        categoryId,
        description,
        categories,
        errorMessage,
        handleTitleChange,
        handleDueDateChange,
        handlePriorityChange,
        handleCategoryChange,
        handleDescriptionChange,
        handleSubmit
    } = useSubtaskCreatePresenter(parentId);

    const navigate = useNavigate();

    const handleCancel = () => {
        navigate("/");
    };

    return(
        <>
            <h2>サブタスク作成</h2>

            <TaskForm
                mode = "subtask"
                isSubtask = {true}

                title = {title}
                dueDate = {dueDate}
                priority = {priority}
                categoryId = {categoryId}
                description = {description}
                categories = {categories}
                errorMessage = {errorMessage}

                onTitleChange = {handleTitleChange}
                onDueDateChange = {handleDueDateChange}
                onPriorityChange = {handlePriorityChange}
                onCategoryChange = {handleCategoryChange}
                onDescriptionChange = {handleDescriptionChange}
                onCancel={handleCancel}

                onSubmit = {handleSubmit}
            />
        </>
    );
}

export default SubtaskCreatePage;