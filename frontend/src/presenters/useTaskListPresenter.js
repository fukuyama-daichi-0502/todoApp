import { useEffect, useState } from "react";
import { getTasks, getSubtasks, deleteTask, updateTaskStatus, reorderTask } from "../models/taskApi";
import { getCategories } from "../models/categoryApi";
import { getApiErrorMessage } from "../errors/apiError";



export const useTaskListPresenter = () => {
    const [tasks, setTasks] = useState([]);
    const [categories, setCategories] = useState([]);
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoading, setIsLoading] = useState(true);

    const [expandedParentId, setExpandedParentId] = useState(null);
    const [subtasksByParent, setSubtasksByParent] = useState({});

    //フィルター
    const [titleSearch, setTitleSearch] = useState("");
    const [statusFilter, setStatusFilter] = useState("");
    const [dueDateFilter, setDueDateFilter] = useState("");
    const [priorityFilter, setPriorityFilter] = useState("");
    const [categoryFilter, setCategoryFilter] = useState("");

    //検索・フィルタリング中かの確認
    const isReorderDisabled =
        titleSearch.trim() !== ""
        || statusFilter !== ""
        || dueDateFilter !== ""
        || priorityFilter !== ""
        || categoryFilter !== "";

    useEffect(() => {
        let ignore = false;
        const fetchTasks = async () => {
            try{
                const data = await getTasks({
                    title: titleSearch,
                    status: statusFilter,
                    dueDate: dueDateFilter,
                    priority: priorityFilter,
                    category: categoryFilter
                });

                if(!ignore){
                    setTasks(data);

                    //検索フィルター中
                    if(isReorderDisabled){
                        const searchedSubtasksByParent = {};

                        for(const task of data){
                            //親タスクは除外
                            if(task.parentTaskId === null){
                                continue;
                            }

                            //親IDの配列が存在しない場合
                            if(searchedSubtasksByParent[task.parentTaskId] === undefined){
                                searchedSubtasksByParent[task.parentTaskId] = [];
                            }

                            searchedSubtasksByParent[task.parentTaskId].push(task);
                        }

                        setSubtasksByParent(searchedSubtasksByParent);

                    }else{
                        setSubtasksByParent({});

                        setExpandedParentId(null);
                    }       
                }

            }catch(error){
                if(!ignore){
                    const message = getApiErrorMessage(error, "タスク一覧の取得に失敗しました");
                    setErrorMessage(message);
                }
                          
            }finally{
                if(!ignore){
                    setIsLoading(false);
                }              
            }
        };

        fetchTasks();

        return () => {
            ignore = true;
        };
        
    },[
        titleSearch,
        statusFilter,
        dueDateFilter,
        priorityFilter,
        categoryFilter,
        isReorderDisabled
    ]);

    //カテゴリ取得用
    useEffect(() => {

        const fetchCategories = async () => {

            try {

                const categoryData = await getCategories();

                setCategories(categoryData);

            } catch (error) {

                const message = getApiErrorMessage(error, "カテゴリの取得に失敗しました");

                setErrorMessage(message);
            }
        };

        fetchCategories();

    }, []);

    const handleDeleteTask = async (id, parentTaskId) => {

         setErrorMessage("");
        try{
            await deleteTask(id);
            setTasks((currentTasks) => currentTasks.filter((task) => task.id !== id));

            if(parentTaskId !== null && parentTaskId !== undefined){
                setSubtasksByParent ((currentSubtasks) => {
                    const newSubtasksByParent = {
                        ...currentSubtasks
                    };

                    const parentSubtasks = currentSubtasks[parentTaskId];

                    if(parentSubtasks !== undefined){
                        const newParentSubtasks = parentSubtasks.filter((subtask) => {
                            return subtask.id !== id;
                        });

                        newSubtasksByParent[parentTaskId] = newParentSubtasks;
                    }

                    return newSubtasksByParent;
                });

            }
            return true;
        }catch(error){
            const message = getApiErrorMessage(error, "タスクの削除に失敗しました");

            setErrorMessage(message);

            return false;
        }
    };

    const handleStatusChange = async(id, newStatus, parentTaskId) => {


        try{
            const isSubtask = parentTaskId !== null && parentTaskId !== undefined;
            //サブタスクが存在するかの確認
            if(!isSubtask && newStatus === "DONE"){
                const subtasks = await getSubtasks(id);
                if(subtasks.length > 0){
                    const confirmed = window.confirm("このタスクを完了にすると、すべてのサブタスクも完了になります\n完了に変更しますか？");
                
                    if(!confirmed){
                        return false;
                    }
                }
            }

            await updateTaskStatus(id, newStatus);
            // setTasks((currentTasks) => currentTasks.map((task) => task.id === updatedTask.id?updatedTask: task));
            const data = await getTasks({
                title: titleSearch,
                status: statusFilter,
                dueDate: dueDateFilter,
                priority: priorityFilter,
                category: categoryFilter
            });

            setTasks(data);

            let refreshParentId = null;

            //サブタスク自体のステータスを変更した場合
            if(parentTaskId !== null && parentTaskId !== undefined){
                refreshParentId = parentTaskId;
            }

            //親タスクをDONEにした時
            else if(newStatus === "DONE"){
                refreshParentId = id;
            }

            


            //親のサブタスクをすでに取得済みなら再取得
            if(refreshParentId !== null &&subtasksByParent[refreshParentId] !== undefined){
                const updatedSubtasks = await getSubtasks(refreshParentId);

                setSubtasksByParent((currentSubtasks) => {
                    const newSubtasksByParent  = {...currentSubtasks};
                    newSubtasksByParent[refreshParentId] = updatedSubtasks;
                    return newSubtasksByParent;
                })
            }

        }catch(error){
            const message = getApiErrorMessage(error, "ステータスの更新に失敗しました");
            setErrorMessage(message);
            
            return false;
        }
    };

    //サブタスク表示
    const handleSubtasksToggle = async (parentId) => {

        //すでにサブタスクを表示している親タスクの時
        if(expandedParentId === parentId) {
            setExpandedParentId(null);
            return;
        }

        try{
            if(subtasksByParent[parentId] === undefined){
                const subtasks = await getSubtasks(parentId);

                setSubtasksByParent((currentSubtasks) => {
                    const newSubtasksByParent = {
                        ...currentSubtasks
                    };
                    newSubtasksByParent[parentId] = subtasks;
                    return newSubtasksByParent;
                });
            }

            setExpandedParentId(parentId);
        }catch (error){
            const message = getApiErrorMessage(error, "サブタスク一覧の取得に失敗しました");

            setErrorMessage(message);
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

        //移動するタスクを取得
        const movingTask = tasks.find((task) => {
            return task.id === taskId;
        });

        if(movingTask === undefined){
            return;
        }

        const movingTaskIsParent = movingTask.parentTaskId === null;

        let reorderTasks;

        if(movingTaskIsParent){
            reorderTasks = tasks.filter((task) =>{
                return task.parentTaskId === null;
            });

        }else{
            reorderTasks = subtasksByParent[movingTask.parentTaskId];

            if(reorderTasks === undefined){
                return;
            }
        }

        if(newIndex < 0 || newIndex >= reorderTasks.length){
            return;
        }

        const targetTask = reorderTasks[newIndex];

        //移動対象自信がtargetの場合は何もしない
        if(targetTask == null || targetTask.id === taskId){
            return;
        }

        const targetTaskIsParent = targetTask.parentTaskId === null;

        let targetTaskId = targetTask.id;

        //親タスクをサブタスクの上に移動させようとしたときサブタスクの親を対象にする
        if(movingTaskIsParent && !targetTaskIsParent){
            targetTaskId = targetTask.parentTaskId;
        }else if(!movingTaskIsParent && targetTaskIsParent){
            return;
        }else if(!movingTaskIsParent && !targetTaskIsParent){
            if(movingTask.parentTaskId !== targetTask.parentTaskId){
                return;
            }
        }

        if(targetTaskId === taskId){
            return;
        }

        const position = newIndex < initialIndex ? "BEFORE" : "AFTER";

        const request = {
            taskId,
            targetTaskId,
            position
        };

        try {
            await reorderTask(request);

            //DBに保存された正式な順番を再取得
            const data = await getTasks();

            setTasks(data);

            //サブタスクを並べ替えた場合
            if(!movingTaskIsParent){
                const parentId = movingTask.parentTaskId;

                const updatedSubtasks = await getSubtasks(parentId);

                setSubtasksByParent((currentSubtasks) => {
                    const newSubtasksByParent = {
                        ...currentSubtasks
                    };

                    newSubtasksByParent[parentId] = updatedSubtasks;

                    return newSubtasksByParent;
                });
            }

        }catch(error){
            const message= getApiErrorMessage(error, "タスクの並び替えに失敗しました");
            setErrorMessage(message)
        }
    }     

    return{
        tasks,
        categories,

        today,

        expandedParentId,
        subtasksByParent,
        
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
        handleSubtasksToggle,
        handleStatusFilterChange,
        handleDueDateFilterChange,
        handlePriorityFilterChange,
        handleCategoryFilterChange
    };

};