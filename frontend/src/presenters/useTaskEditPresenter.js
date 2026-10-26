import { useEffect, useState } from "react";
import { getTask, getSubtasks, updateTask } from "../models/taskApi";
import { getCategories } from "../models/categoryApi";
import { getApiErrorMessage } from "../errors/apiError";

export const useTaskEditPresenter = (id) => {
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [status, setStatus] = useState("");
    const [dueDate, setDueDate] = useState("");
    const [priority, setPriority] = useState("");
    const [categoryId, setCategoryId] = useState("");
    const [categories, setCategories] = useState([]);
    const [recurrenceRule, setRecurrenceRule] = useState("NONE");
    const [recurrenceBaseDay, setRecurrenceBaseDay] = useState("");
    const [isSubtask, setIsSubtask] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const [originalStatus, setOriginalStatus] = useState("");
    const [hasSubtasks, setHasSubtasks] = useState(false);
    
    useEffect(() => {
        const fetchTask = async() =>{
            try{
                const task = await getTask(id);

                setTitle(task.title);
                setDescription(task.description ?? "");
                setStatus(task.status);
                setOriginalStatus(task.status);
                setDueDate(task.dueDate ?? "");
                setPriority(task.priority ?? "");
                setCategoryId(task.categoryId == null ? "" : String(task.categoryId));
                setRecurrenceRule(task.recurrenceRule ?? "NONE");
                setRecurrenceBaseDay(task.recurrenceBaseDay ?? "");

                const subtask = task.parentTaskId !== null;
                setIsSubtask(task.parentTaskId !== null);

                if(!subtask){
                    const subtasks = await getSubtasks(id);
                    setHasSubtasks(subtasks.length > 0);
                }else{
                    setHasSubtasks(false);
                }

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

    const handleRecurrenceRuleChange = (event) => {
        const value = event.target.value;
        setRecurrenceRule(value);

        if(value !== "MONTHLY"){
            setRecurrenceBaseDay("");
        }
    };

    const handleRecurrenceBaseDayChange = (day) => {
        setRecurrenceBaseDay(day);
    };

    const handleUpdateTask = async (event) => {
        event.preventDefault();
        setErrorMessage("");

        const changedToDone = originalStatus !== "DONE" && status === "DONE";

        if(!isSubtask && hasSubtasks && changedToDone){
            const confirmed = window.confirm("このタスクを完了にすると、すべてのサブタスクも完了になります\n完了に変更しますか？")
        
            if(!confirmed){
                return null;
            }
        }

        const request = {
            title,
            description,
            status,
            dueDate: dueDate === "" ? null : dueDate,
            priority: priority === "" ? null : priority,
            categoryId: categoryId === "" ? null : Number(categoryId),
            recurrenceRule: isSubtask ? "NONE" : recurrenceRule,
            recurrenceBaseDay: !isSubtask && recurrenceRule === "MONTHLY" && recurrenceBaseDay !== "" ? Number(recurrenceBaseDay) : null,
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
        recurrenceRule,
        recurrenceBaseDay,
        isSubtask,
        errorMessage,
        isLoading,
        handleTitleChange,
        handleDescription,
        handleStatusChange,
        handleDueDateChange,
        handlePriorityChange,
        handleCategoryChange,
        handleUpdateTask,
        handleRecurrenceRuleChange,
        handleRecurrenceBaseDayChange
    };
}