import { useState, useEffect } from "react";
import { createTask } from "../models/taskApi";
import { getCategories } from "../models/categoryApi";
import { getApiErrorMessage } from "../errors/apiError";

export const useTaskCreatePresenter = () => {

    const [title, setTitle] = useState("");
    const [dueDate, setDueDate] = useState("");
    const [priority, setPriority] = useState("");
    const [categoryId, setCategoryId] = useState("");

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
    };

    const handleDueDateChange = (event) => {
        setDueDate(event.target.value);
    }

    const handlePriorityChange = (event) => {
        setPriority(event.target.value);
    }

    const handleCategoryChange = (event) => {
        setCategoryId(event.target.value);
    }

    const handleCreateTask = async (event) => {

        event.preventDefault();

        setErrorMessage("");

        const request = {
            title: title,
            dueDate: dueDate === "" ? null : dueDate,
            priority: priority === "" ? null : priority,
            categoryId: categoryId === "" ? null : Number(categoryId)
        };

        try {

            const createdTask = await createTask(request);

            return createdTask;

        } catch (error) {

            const message = getApiErrorMessage(error, "タスクの作成に失敗しました");

            setErrorMessage(message);

            return null;
        }
    };

    return {
        title,
        dueDate,
        priority,
        categoryId,
        categories,
        errorMessage,

        handleTitleChange,
        handleDueDateChange,
        handlePriorityChange,
        handleCategoryChange,
        handleCreateTask
    };
};