import { useEffect, useState } from "react";
import { getTask, updateTask } from "../models/taskApi";
import { getApiErrorMessage } from "../errors/apiError";

export const useTaskEditPresenter = (id) => {
    const [title, setTitle] = useState("");
    const [status, setStatus] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    
    useEffect(() => {
        const fetchTask = async() =>{
            try{
                const task = await getTask(id);

                setTitle(task.title);
                setStatus(task.status);
            }catch(error){
                const message = getApiErrorMessage(error, "タスクの取得に失敗しました");

                setErrorMessage(message);
            }finally{
                setIsLoading(false);
            }
        };
        
        fetchTask();
    },[id]);

    const handleTitleChange = (event) => {
        setTitle(event.target.value);
    };

    const handleStatusChange = (event) =>{
        setStatus(event.target.value);
    };

    const handleUpdateTask = async (event) =>{
        event.preventDefault();
        setErrorMessage("");
        
        try{
            const updatedTask = await updateTask(id, title, status);
            
            return updatedTask;
        }catch(error){
            const message =  getApiErrorMessage(error, "タスクの更新に失敗しました");
            setErrorMessage(message);

            return null;
        }
    };
    
    return {
        title,
        status,
        errorMessage,
        isLoading,
        handleTitleChange,
        handleStatusChange,
        handleUpdateTask
    };
}