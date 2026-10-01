import { useEffect, useState } from "react";
import { deleteCategory, getCategories } from "../models/categoryApi";
import { getApiErrorMessage} from "../errors/apiError";

export const useCategoryListPresenter = () => {
    const [categories, setCategories] = useState([]);

    const [errorMessage, setErrorMessage] = useState("");

    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const data = await getCategories();
                setCategories(data);
            } catch(error) {
                const message = getApiErrorMessage(error, "カテゴリ一覧の取得に失敗しました");
                setErrorMessage(message);
            } finally {
                setIsLoading(false);
            }
        };
        fetchCategories();
    }, []);

    const handleDelete = async (id) => {
        try {
            await deleteCategory(id);

            setCategories((prevCategories) => prevCategories.filter((category) => category.id !== id));
        }catch (error) {
            const message = getApiErrorMessage(error, "カテゴリの削除に失敗しました");

            setErrorMessage(message);
        }
    };

    return {
        categories,
        errorMessage,
        isLoading,
        handleDelete
    };
        
}