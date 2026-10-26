import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { createSubtask } from "../models/taskApi";
import { getCategories } from "../models/categoryApi";
import { getApiErrorMessage } from "../errors/apiError";

export const useSubtaskCreatePresenter = (parentId) => {
    const navigate = useNavigate();

    const [title, setTitle] = useState("");
    const [dueDate, setDueDate] = useState("");
    const [priority, setPriority] = useState("");
    const [categoryId, setCategoryId] = useState("");
    const [description, setDescription] = useState("");

    const [categories, setCategories] = useState([]);
    
    const [errorMessage, setErrorMessage] = useState("");

    useEffect (() => {
            const fetchCategories = async () => {
                try{
                    const data = await getCategories();
    
                    setCategories(data);
                }catch(error){
                    setErrorMessage(getApiErrorMessage(error, "カテゴリ一覧の取得に失敗しました"));
                }
            };
    
            fetchCategories();
    
    },[]);

    const handleTitleChange = (event) => {
        setTitle(event.target.value);
    }

    const handleDueDateChange = (event) => {
        setDueDate(event.target.value);
    }

    const handlePriorityChange = (event) => {
        setPriority(event.target.value);
    }

    const handleCategoryChange = (event) => {
        setCategoryId(event.target.value);
    }

    const handleDescriptionChange = (event) => {
        setDescription(event.target.value);
    }

    const handleSubmit = async (event) => {
        event.preventDefault()

        try {
            const createdSubtask = await createSubtask(
                parentId,
                {
                    title,
                    dueDate: dueDate === "" ? null : dueDate,
                    priority: priority === "" ? null : priority,
                    categoryId: categoryId === "" ? null : Number(categoryId),
                    description: description === "" ? null : description
                }
            );

            if(createdSubtask){
                navigate("/");
            }
        } catch (error) {
            const message = getApiErrorMessage(error, "サブタスクの登録に失敗しました");
            
            setErrorMessage(message);
        }
    };

    return {
        title,
        dueDate,
        priority,
        categoryId,
        description,
        errorMessage,

        categories,

        handleTitleChange,
        handleDueDateChange,
        handlePriorityChange,
        handleCategoryChange,
        handleDescriptionChange,
        handleSubmit
    };
};

