import { useEffect, useState } from "react";
import{ useParams } from "react-router-dom";
import{ getTask, updateTaskDescription } from "../models/taskApi";
import{ getApiErrorMessage } from "../errors/apiError";

export const useTaskDetailPresenter = () => {
    const { id } = useParams();
    const [task, setTask,] = useState(null);
    const [description, setDescription] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);
    const [loadErrorMessage, setLoadErrorMessage] = useState("");
    const [saveErrorMessage, setSaveErrorMessage] = useState("");
    const [saveMessage, setSaveMessage] = useState("");

    useEffect(() => {
        const fetchTask = async () => {
            try{
                const data = await getTask(id);
                setTask(data);
                setDescription(data.description ?? "");
            }catch(error){
                const message = getApiErrorMessage(error, "タスクの取得に失敗しました");
                setLoadErrorMessage(message);
            }finally{
                setIsLoading(false);
            }
        };
        
        fetchTask();
    }, [id]);

    const handleDescriptionChange = (event) => {
        setDescription(event.target.value);
        setSaveMessage("");
    }

    const handleDescriptionSave = async () => {
        setIsSaving(true);
        setSaveErrorMessage("");
        setSaveMessage("");

        const request = {description};

        try{
            const updatedTask = await updateTaskDescription(id, request);

            setTask(updatedTask);
            setDescription(updatedTask.description ?? "");
            setSaveMessage("詳細を保存しました");
        }catch (error) {
            const message = getApiErrorMessage(error, "詳細の保存に失敗しました");
            setSaveErrorMessage(message);
        }finally {
            setIsSaving(false);
        }
    };

    return{
        task,
        description,

        isLoading,
        isSaving,
        
        loadErrorMessage,
        saveErrorMessage,
        saveMessage,

        handleDescriptionChange,
        handleDescriptionSave
    }
}