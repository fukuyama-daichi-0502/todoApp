import TodoList from "../components/TodoList";
import { useTaskListPresenter } from "../presenters/useTaskListPresenter";
import {Link} from "react-router-dom";

import "./TodoPage.css";

function TodoPage() {
    const {
        tasks,
        categories,
        expandedParentId,
        subtasksByParent,
        today,
        titleSearch,
        statusFilter,
        dueDateFilter,
        priorityFilter,
        categoryFilter,
        isReorderDisabled,
        errorMessage,
        isLoading,
        handleDeleteTask,
        handleStatusChange,
        handleTitleSearchChange,
        handleStatusFilterChange,
        handleDueDateFilterChange,
        handlePriorityFilterChange,
        handleCategoryFilterChange,
        handleTaskReorder,
        handleSubtasksToggle
    } = useTaskListPresenter();

    let taskListContent;

    const handleDelete = async(id, parentTaskId) => {
        const confirmad = window.confirm("このタスクを削除しますか？");

        if(!confirmad){
            return;
        }

        await handleDeleteTask(id, parentTaskId);
    };
    
    if (isLoading) {
        taskListContent = <p>読み込み中...</p>;
    }else if(errorMessage){
        taskListContent = <p>{errorMessage}</p>; 
    }else{
        taskListContent = 
            <TodoList
                tasks = {tasks}
                today = {today}
                titleSearch={titleSearch}
                statusFilter={statusFilter}
                dueDateFilter={dueDateFilter}
                priorityFilter={priorityFilter}
                categoryFilter={categoryFilter}
                categories={categories}
                expandedParentId = {expandedParentId}
                subtasksByParent = {subtasksByParent}
                isReorderDisabled={isReorderDisabled}
                handleTitleSearchChange={handleTitleSearchChange}
                handleStatusFilterChange={handleStatusFilterChange}
                handleDueDateFilterChange={handleDueDateFilterChange}
                handlePriorityFilterChange={handlePriorityFilterChange}
                handleCategoryFilterChange={handleCategoryFilterChange}
                onTaskReorder={handleTaskReorder}
                onDelete = {handleDelete}
                onStatusChange = {handleStatusChange}
                onSubtasksToggle = {handleSubtasksToggle}
            />;
    }

    

    return(
        <div>
            <h1>Todoアプリ</h1>
            <div class = "task-list-header">            
                <h2>タスク一覧</h2>
                <Link class = "button" to = "/tasks/new">
                    タスク追加
                </Link>
                <Link className = "button" to = "/categories">
                    カテゴリ一覧
                </Link>
            </div>

            {taskListContent}
            
        </div>
    );

    

}

export default TodoPage;