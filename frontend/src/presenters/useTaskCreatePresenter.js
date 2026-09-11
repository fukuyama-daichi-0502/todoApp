import { useState } from "react";
import { createTask } from "../models/taskApi";
import { getApiErrorMessage } from "../errors/apiError";

export const useTaskCreatePresenter = () => {

    const [title, setTitle] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

    const handleTitleChange = (event) => {
        setTitle(event.target.value);
    };

    const handleCreateTask = async (event) => {

        event.preventDefault();

        setErrorMessage("");

        try {

            const createdTask = await createTask(title);

            setTitle("");

            return createdTask;

        } catch (error) {

            const message = getApiErrorMessage(error, "タスクの作成に失敗しました");

            setErrorMessage(message);

            return null;
        }
    };

    return {title,errorMessage,handleTitleChange,handleCreateTask};
};