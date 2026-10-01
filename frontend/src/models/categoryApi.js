import axios from "axios";

const CATEGORY_API_URL = "http://localhost:8080/api/categories";

export const getCategories = async () => {
    const response = await axios.get(CATEGORY_API_URL);

    return response.data;
};

export const createCategory = async (request) => {
    const response = await axios.post(CATEGORY_API_URL, request);

    return response.data;
};

export const deleteCategory = async (id) => {
    await axios.delete(`${CATEGORY_API_URL}/${id}`);
};