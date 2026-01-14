// src/components/SearchBar.jsx
import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import './SearchBar.css';
import TagLabel from './TagLabel';
import { splitBracketName } from '../utils/nameFormat';

export default function SearchBar({ onUserSelect, onSearchResults }) {
  const [keyword, setKeyword] = useState('');
  const [results, setResults] = useState([]);
  const [isOpen, setIsOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const searchRef = useRef(null);

  useEffect(() => {
    if (keyword.trim() === '') {
      setResults([]);
      setIsOpen(false);
      // 검색어가 비어있을 때 검색 결과 초기화
      if (typeof onSearchResults === 'function') {
        onSearchResults(null);
      }
      return;
    }

    const timeoutId = setTimeout(() => {
      searchUsers(keyword);
    }, 500);

    return () => clearTimeout(timeoutId);
  }, [keyword]);

  // 외부 클릭 시 검색 결과 닫기
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (searchRef.current && !searchRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const searchUsers = async (searchKeyword) => {
    if (!searchKeyword.trim()) return;

    setLoading(true);
    try {
      const response = await axios.get('/api/search/users', {
        params: { keyword: searchKeyword }
      });
      setResults(response.data);
      setIsOpen(true);
      // 검색 결과를 우측 화면에 표시
      if (typeof onSearchResults === 'function') {
        onSearchResults(response.data);
      }
    } catch (error) {
      setResults([]);
      if (typeof onSearchResults === 'function') {
        onSearchResults(null);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleUserClick = (user) => {
    // 검색 결과 클릭 시 해당 부서로 이동
    if (typeof onUserSelect === 'function') {
      onUserSelect(user);
    }
    setKeyword('');
    setResults([]);
    setIsOpen(false);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Escape') {
      setIsOpen(false);
    }
    // Enter 키로 첫 번째 결과 선택
    if (e.key === 'Enter' && results.length > 0) {
      handleUserClick(results[0]);
    }
  };

  const handleInputChange = (e) => {
    setKeyword(e.target.value);
  };

  return (
    <div className="search-container" ref={searchRef}>
      <div className="search-input-wrapper">
        <input
          type="text"
          placeholder="이름 또는 ID로 검색..."
          value={keyword}
          onChange={handleInputChange}
          onKeyDown={handleKeyDown}
          className="search-input"
        />
        {loading && <div className="search-loading">검색 중...</div>}
      </div>

      {isOpen && results.length > 0 && (
        <div className="search-results">
          {results.map((user) => {
            const info = splitBracketName(user.userName);

            return (
              <div
                key={user.userId}
                className="search-result-item"
                onClick={() => handleUserClick(user)}
              >
                <div className="user-info">
                  {user.photoUrl ? (
                    <img
                      src={user.photoUrl}
                      alt={user.userName}
                      className="user-avatar"
                      onError={(e) => (e.currentTarget.src = '/default-avatar.png')}
                    />
                  ) : (
                    <div className="user-avatar-placeholder" />
                  )}
                  <div className="user-details">
                    <div className="user-name">
                      {info.hasTag ? (
                        <>
                          <TagLabel tagInfo={info} className="search-result__tag" />
                          {info.text && (
                            <span className="search-result__name-text">{info.text}</span>
                          )}
                        </>
                      ) : (
                        <span className="search-result__name-text">{user.userName}</span>
                      )}
                      <span className="user-id">#{user.userId}</span>
                    </div>
                    <div className="user-position">{user.positionName || ''}</div>
                    <div className="user-department">{user.departmentName}</div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {isOpen && results.length === 0 && keyword.trim() && !loading && (
        <div className="search-results">
          <div className="no-results">검색 결과가 없습니다.</div>
        </div>
      )}
    </div>
  );
}