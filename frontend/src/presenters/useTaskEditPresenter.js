import { useEffect, useState } from "react";
import { getTask, updateTask } from "../models/taskApi";
import { getCategories } from "../models/categoryApi";
import { getApiErrorMessage } from "../errors/apiError";

export const useTaskEditPresenter = (id) => {
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [status, setStatus] = useState("");
    const [dueDate, setDueDate] = useState("");
    const [priority, setPriority] = useState("MEDIUM");
    const [categoryId, setCategoryId] = useState("");
    const [categories, setCategories] = useState([]);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    
    useEffect(() => {
        const fetchTask = async() =>{
            try{
                const task = await getTask();

                setTitle(task.title);
                setDescription(task.description ?? "");
                setStatus(task.status);
                setDueDate(task.dueDate ?? "");
                setPriority(task.priority ?? "MEDIUM");
                setCategoryId(task.categoryId == null ? "" : String(task.categoryId))

                const categoryData = await getCategories();
                setCategories(categoryData);

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

    const handleDescription = (event) => {
        setDescription(event.target.value);
    }

    const handleStatusChange = (event) => {
        setStatus(event.target.value);
    };

    const handleDueDateChange = (event) => {
        setDueDate(event.target.value);
    };

    const handlePriorityChange = (event) => {
        setPriority(event.target.value);
    };

    const handleCategoryChange = (event) => {
        setCategoryId(event.target.value);
    };

    const handleUpdateTask = async (event) => {
        event.preventDefault();
        setErrorMessage("");

        const request = {
            title,
            description,
            status,
            dueDate: dueDate === "" ? null : dueDate,
            priority,
            categoryId: categoryId === "" ? null : Number(categoryId),
        };
        
        try{
            const updatedTask = await updateTask(id,request);
            
            return updatedTask;
        }catch(error){
            const message =  getApiErrorMessage(error, "タスクの更新に失敗しました");
            setErrorMessage(message);

            return null;
        }
    };
    
    return {
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
    };
}