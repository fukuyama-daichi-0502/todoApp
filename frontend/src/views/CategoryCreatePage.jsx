import { Link } from "react-router-dom";
import CategoryForm from "../components/CategoryForm";
import { useCategoryCreatePresenter } from "../presenters/useCategoryCreatePresenter";

function CategoryCreatePage() {
    const {
        name,
        colorCode,
        errorMessage,

        handleNameChange,
        handleColorCodeChange,
        handleSubmit
    } = useCategoryCreatePresenter();

    return (
        <div>
            <h1>
                新規カテゴリ追加
            </h1>

            <CategoryForm mode = "create" name = {name} colorCode = {colorCode} errorMessage = {errorMessage} onNameChange = {handleNameChange} onColorCodeChange = {handleColorCodeChange} onSubmit = {handleSubmit}/>

            <Link to = "/categories">
                キャンセル
            </Link>
        </div>
    );
}

export default CategoryCreatePage;