import { Link } from "react-router-dom";
import CategoryList from "../components/CategoryList";
import { useCategoryListPresenter } from "../presenters/useCategoryListPresenter";

import "./CategoryPage.css";

function CategoryPage() {
    const {
        categories,
        errorMessage,
        isLoading,
        handleDelete
    } = useCategoryListPresenter();

    let categoryListContent;

    if (isLoading) {
        categoryListContent = (<p>読み込み中...</p>);
    } else if (errorMessage) {
        categoryListContent = ( <p>{errorMessage}</p>);
    } else if (categories.length === 0) {
        categoryListContent = (<p>カテゴリがありません</p>);
    } else {
        categoryListContent = (<CategoryList categories = {categories} onDelete = {handleDelete}/>);
    }

    return (
        <div>
            <h1>
                Todoアプリ
            </h1>

            <div className = "category-list-header">
                <h2>
                    カテゴリ一覧
                </h2>

                <Link className = "button" to = "/categories/new">
                    カテゴリ追加
                </Link>
            </div>
            

            {categoryListContent}

            <Link to = "/">
                タスク一覧へ戻る
            </Link>
        </div>
    );
}

export default CategoryPage;