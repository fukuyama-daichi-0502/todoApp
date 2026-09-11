import TodoList from "../components/TodoList";
import { useTaskListPresenter } from "../presenters/useTaskListPresenter";
import {Link} from "react-router-dom";

import "./TodoPage.css";

function TodoPage() {
    const { tasks, errorMessage, isLoading, handleDeleteTask, handleStatusChange } = useTaskListPresenter();

    let taskListContent;

    const handleDelete = async(id) => {
        const confirmad = window.confirm("このタスクを削除しますか？");

        if(!confirmad){
            return;
        }

        await handleDeleteTask(id);
    };
    
    if (isLoading) {
        taskListContent = <p>読み込み中...</p>;
    }else if(errorMessage){
        taskListContent = <p>{errorMessage}</p>; 
    }else{
        taskListContent = <TodoList tasks = {tasks} onDelete = {handleDelete} onStatusChange = {handleStatusChange}/>;
    }

    

    return(
        <div>
            <h1>Todoアプリ</h1>
            <div class = "task-list-header">            
                <h2>タスク一覧</h2>
                <Link class = "button" to = "/tasks/new">
                    タスク追加
                </Link>
            </div>

            {taskListContent}
            
        </div>
    );

    

}

export default TodoPage;