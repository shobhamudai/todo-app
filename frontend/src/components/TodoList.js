import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { fetchAuthSession } from 'aws-amplify/auth';

const API_URL = 'https://dctvfnojaf.execute-api.us-east-1.amazonaws.com/prod/todos';

const TodoList = () => {
    const [todos, setTodos] = useState([]);
    const [task, setTask] = useState('');

    async function getAxiosConfig() {
        const session = await fetchAuthSession();
        const token = session.tokens?.idToken?.toString();
        return {
            headers: {
                'Authorization': `Bearer ${token}`,
            }
        };
    }

    useEffect(() => {
        fetchTodos();
    }, []);

    const fetchTodos = async () => {
        const config = await getAxiosConfig();
        axios.get(API_URL, config).then(response => {
            setTodos(response.data);
        });
    };

    const addTodo = async () => {
        const config = await getAxiosConfig();
        axios.post(API_URL, { task }, config).then(() => {
            fetchTodos();
            setTask('');
        });
    };

    const toggleTodo = async (id) => {
        const config = await getAxiosConfig();
        const todo = todos.find(todo => todo.id === id);
        axios.put(`${API_URL}/${id}`, { ...todo, completed: !todo.completed }, config).then(() => {
            fetchTodos();
        });
    };

    const deleteTodo = async (id) => {
        const config = await getAxiosConfig();
        axios.delete(`${API_URL}/${id}`, config).then(() => {
            fetchTodos();
        });
    };

    const formatTimestamp = (epoch) => {
        if (!epoch) return null;
        return new Date(epoch).toLocaleString();
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
                    onKeyPress={(e) => e.key === 'Enter' && addTodo()}
                />
                <div className="input-group-append">
                    <button className="btn btn-primary" onClick={addTodo}>Add</button>
                </div>
            </div>
            <ul className="list-group">
                {todos.map(todo => (
                    <li key={todo.id} className={`list-group-item d-flex justify-content-between align-items-center`}>
                        <div>
                            <span
                                style={{ textDecoration: todo.completed ? 'line-through' : 'none', cursor: 'pointer' }}
                                onClick={() => toggleTodo(todo.id)}
                            >
                                {todo.task}
                            </span>
                            {todo.createdAt && (
                                <div className="text-muted small">{formatTimestamp(todo.createdAt)}</div>
                            )}
                        </div>
                        <button className="btn btn-danger" onClick={() => deleteTodo(todo.id)}>Delete</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default TodoList;
