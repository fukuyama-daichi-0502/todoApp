import axios from "axios";

const Base_URL ="http://localhost:8080/api/tasks";

export const getTasks = async () => {
    const response = await axios.get(Base_URL);

    return response.data;
};

export const createTask = async (request) => {
    const response = await axios.post(Base_URL, request);

    return response.data;
};

export const updateTask = async(id, request) => {
    
    const response = await axios.put(`${Base_URL}/${id}`,request);

    return response.data;
};

export const updateTaskDescription = async(id, request) => {
    const response = await axios.patch(`${Base_URL}/${id}/description`, request);

    return response.data;
}

export const updateTaskStatus = async(id, status) => {
    const response = await axios.patch(
        `${Base_URL}/${id}/status`,
        {
            status: status
        }
    );

    return response.data;
};

export const getTask = async(id) => {
    const response = await axios.get(`${Base_URL}/${id}`);

    return response.data;
};

export const deleteTask = async(id) => {
    await axios.delete(`${Base_URL}/${id}`);
};

export const reorderTask = async (request) => {
    await axios.patch(`${Base_URL}/reorder`, request);
};