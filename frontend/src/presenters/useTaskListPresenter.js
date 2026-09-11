import { useEffect, useState } from "react";
import { getTasks, deleteTask, updateTaskStatus } from "../models/taskApi";
import { getApiErrorMessage } from "../errors/apiError";

export const useTaskListPresenter = () => {
    const [tasks, setTasks] = useState([]);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchTasks = async () => {
            try{
                const data = await getTasks();

                setTasks(data);
            
            }catch(error){
                const message = getApiErrorMessage(error, "タスク一覧の取得に失敗しました");
            
                setErrorMessage(message);
            }finally{
                setIsLoading(false);
            }
            
        };

        fetchTasks();
    },[]);

    const handleDeleteTask = async (id) => {
         setErrorMessage("");
        try{
            await deleteTask(id);
            setTasks((currentTasks) => currentTasks.filter((task) => task.id !== id));

            return true;
        }catch(error){
            const message = getApiErrorMessage(error, "タスクの削除に失敗しました");

            setErrorMessage(message);

            return false;
        }
    };

    const handleStatusChange = async(id, newStatus) => {
        setErrorMessage("");

        try{
            const updatedTask = await updateTaskStatus(id, newStatus);
            setTasks((currentTasks) => currentTasks.map((task) => task.id === updatedTask.id?updatedTask: task));
            
            return true;
        }catch(error){
            const message = getApiErrorMessage(error, "ステータスの更新に失敗しました");
            setErrorMessage(message);
            
            return false;
        }
    };

    return{
        tasks,
        errorMessage,
        isLoading,
        handleDeleteTask,
        handleStatusChange
    };

};