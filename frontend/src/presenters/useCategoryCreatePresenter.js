import { useState } from "react";
import { useNavigate} from "react-router-dom";
import { createCategory } from "../models/categoryApi"
import { getApiErrorMessage } from "../errors/apiError"

export const useCategoryCreatePresenter = () => {
    const navigate = useNavigate();
    const [name, setName,] = useState();
    const [colorCode, setColorCode] = useState("#000000");
    const [errorMessage, setErrorMessage] = useState("");
    
    const handleNameChange = (event) => {
        setName(event.target.value);
    };

    const handleColorCodeChange = (event) => {
        setColorCode(event.target.value);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        const request = {name, colorCode};

        try {
            const createdCategory = await createCategory(request);

            if (createdCategory) {
                navigate("/categories");
            }
        }catch (error) {
            const message = getApiErrorMessage(error, "カテゴリの登録に失敗しました");

            setErrorMessage(message);
        }
    };

    return {
        name,
        colorCode,
        errorMessage,

        handleNameChange,
        handleColorCodeChange,
        handleSubmit
    };
};