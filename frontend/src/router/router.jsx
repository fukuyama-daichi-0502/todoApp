import { createBrowserRouter } from "react-router-dom";

import TodoPage from "../views/TodoPage";
import TaskCreatePage from "../views/TaskCreatePage";
import TaskEditPage from "../views/TaskEditPage";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <TodoPage />
    },
    {
        path: "/tasks/new",
        element: <TaskCreatePage />
    },
    {
        path: "/tasks/:id/edit",
        element: <TaskEditPage />
    }
]);