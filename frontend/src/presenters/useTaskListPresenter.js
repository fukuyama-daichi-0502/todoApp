import { useEffect, useState } from "react";
import { getTasks, deleteTask, updateTaskStatus, reorderTask } from "../models/taskApi";
import { getApiErrorMessage } from "../errors/apiError";

export const useTaskListPresenter = () => {
    const [tasks, setTasks] = useState([]);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoading, setIsLoading] = useState(true);

    //フィルター
    const [titleSearch, setTitleSearch] = useState("");
    const [statusFilter, setStatusFilter] = useState("");
    const [dueDateFilter, setDueDateFilter] = useState("");
    const [priorityFilter, setPriorityFilter] = useState("");
    const [categoryFilter, setCategoryFilter] = useState("");

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

    //検索・フィルター変更
    const handleTitleSearchChange = (event) => {
        setTitleSearch(event.target.value);
    };

    const handleStatusFilterChange = (event) => {
        setStatusFilter(event.target.value);
    }

    const handlePriorityFilterChange = (event) => {
        setPriorityFilter(event.target.value);
    }

    const handleDueDateFilterChange = (event) => {
        setDueDateFilter(event.target.value);
    };

    const handleCategoryFilterChange = (event) => {
        setCategoryFilter(event.target.value);
    }

    //本日の日付を取得
    const now = new Date();

    const year = now.getFullYear();

    const month = String(now.getMonth() + 1).padStart(2,"0");

    const day = String(now.getDate()).padStart(2,"0");

    const today = `${year}-${month}-${day}`;



    const categoryOptions = Array.from(
        new Map(
            tasks
                .filter(
                    (task) =>
                        task.categoryId != null
                )
                .map((task) => [
                    task.categoryId,
                    {
                        id: task.categoryId,
                        name: task.categoryName
                    }
                ])
        ).values()
    );

    //画面表示するタスク
    const filteredTasks = tasks.filter((task) => {
        //タイトル検索
        const searchKeyword = titleSearch.trim().toLowerCase();

        if(searchKeyword !== "" && !task.title.toLowerCase().includes(searchKeyword)){
            return false;
        }


        if(statusFilter !== "" && task.status !== statusFilter){
            return false;
        }

        if(priorityFilter !== "" && task.priority !== priorityFilter){
            return false;
        }

        //期限：当日
        if(dueDateFilter === "TODAY" && task.dueDate !== today){
            return false;
        }

        //期限：期限切れ
        if(dueDateFilter === "OVERDUE"){
            if(!task.dueDate || task.dueDate >= today || task.status === "DONE" || task.status === "CANCELLED"){
                return false;
            }
        }

        if(categoryFilter === "NONE"){
            if(task.categoryId != null){
                return false;
            }
        }else if(categoryFilter !== "" && String(task.categoryId) !== categoryFilter){
            return false;
        }
        return true;
    })

    //検索・フィルタリング中かの確認
    const isReorderDisabled =
        titleSearch.trim() !== ""
        || statusFilter !== ""
        || dueDateFilter !== ""
        || priorityFilter !== ""
        || categoryFilter !== "";

    //タスク並び替え
    const handleTaskReorder = async (
        taskId,
        initialIndex,
        newIndex
    ) => {
        
        //検索・フィルター中
        if(isReorderDisabled) {
            return;
        }

        //実質的に移動していない場合
        if(initialIndex === newIndex){
            return;
        }

        //移動先indexが不正な場合
        if(newIndex < 0 || newIndex >= filteredTasks.length){
            return;
        }

        const targetTask = tasks[newIndex];

        //移動対象自信がtargetの場合は何もしない
        if(targetTask == null || targetTask.id === taskId){
            return;
        }

        const position = newIndex < initialIndex ? "BEFORE" : "AFTER";

        const request = {
            taskId,
            targetTaskId: targetTask.id,
            position
        };

        try {
            console.log(
    "並び替えリクエスト:",
    request
);
            await reorderTask(request);

            //DBに保存された正式な順番を再取得
            const data = await getTasks();
            setTasks(data);
        }catch(error){
            const message= getApiErrorMessage(error, "タスクの並び替えに失敗しました");
            setErrorMessage(message)
        }
    }
        

    return{
        tasks,
        filteredTasks,
        categoryOptions,

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
        handleTaskReorder,

        handleTitleSearchChange,
        handleStatusFilterChange,
        handleDueDateFilterChange,
        handlePriorityFilterChange,
        handleCategoryFilterChange
    };

};