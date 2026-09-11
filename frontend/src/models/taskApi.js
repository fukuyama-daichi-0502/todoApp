import axios from "axios";

const Base_URL ="http://localhost:8080/api/tasks";

export const getTasks = async () => {
    const response = await axios.get(Base_URL);

    return response.data;
};

export const createTask = async (title) => {
    const response = await axios.post(Base_URL, {title: title});

    return response.data;
};

export const updateTask = async(id, title, status) => {
    
    const response = await axios.put(
        `${Base_URL}/${id}`,
        {
            title: title,
            status: status
        }
    );

    return response.data;
};

export const updateTaskStatus = async(id, status) => {
    const response = await axios.patch(
        `${Base_URL}/${id}/status`,
        {
            status: status
        }
    );

    return response.data;
};

export const getTask = async(id) =>{
    const response = await axios.get(`${Base_URL}/${id}`);

    return response.data;
};

export const deleteTask = async(id) =>{
    await axios.delete(`${Base_URL}/${id}`);
};