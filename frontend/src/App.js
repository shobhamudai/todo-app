import React, { useState, useEffect } from 'react';
import axios from 'axios';

const API_URL = 'https://dctvfnojaf.execute-api.us-east-1.amazonaws.com/prod/todos';

function App() {
    const [todos, setTodos] = useState([]);
    const [task, setTask] = useState('');

    useEffect(() => {
        fetchTodos();
    }, []);

    const fetchTodos = () => {
        axios.get(API_URL).then(response => {
            console.log(response);
            setTodos(response.data);
        });
    };

    const addTodo = () => {
        axios.post(API_URL, { task }).then(response => {
            fetchTodos();
            setTask('');
        });
    };

    const toggleTodo = (id) => {
        const todo = todos.find(todo => todo.id === id);
        axios.put(`${API_URL}/${id}`, { ...todo, completed: !todo.completed }).then(response => {
            fetchTodos();
        });
    };

    const deleteTodo = (id) => {
        axios.delete(`${API_URL}/${id}`).then(() => {
            fetchTodos();
        });
    };

    return (
        <div className="container mt-5">
            <h1>To-Do List</h1>
            <div className="input-group mb-3">
                <input
                    type="text"
                    className="form-control"
                    value={task}
                    onChange={(e) => setTask(e.target.value)}
                    placeholder="New task"
                />
                <div className="input-group-append">
                    <button className="btn btn-primary" onClick={addTodo}>Add</button>
                </div>
            </div>
            <ul className="list-group">
                {todos.map(todo => (
                    <li key={todo.id} className={`list-group-item d-flex justify-content-between align-items-center`}>
                        <span
                            style={{ textDecoration: todo.completed ? 'line-through' : 'none' }}
                            onClick={() => toggleTodo(todo.id)}
                        >
                            {todo.task}
                        </span>
                        <button className="btn btn-danger" onClick={() => deleteTodo(todo.id)}>Delete</button>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default App;
