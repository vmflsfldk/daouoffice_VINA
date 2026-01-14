// src/components/UserDetailModal.jsx
import React, { useEffect, useState } from 'react';
import { createPortal } from 'react-dom';
import './UserDetailModal.css';
import TagLabel from './TagLabel';
import { splitBracketName } from '../utils/nameFormat';

export default function UserDetailModal({ user, loading, error, onClose }) {
  const [isMounted, setIsMounted] = useState(false);

  useEffect(() => {
    if (typeof document === 'undefined') {
      return undefined;
    }

    setIsMounted(true);
    const { body } = document;
    const previousOverflow = body.style.overflow;
    body.style.overflow = 'hidden';

    return () => {
      body.style.overflow = previousOverflow;
    };
  }, []);

  // overlay 클릭 시 닫히도록
  const stop = e => e.stopPropagation();

  if (!isMounted || typeof document === 'undefined') {
    return null;
  }

  return createPortal(
    <div className="modal-overlay" role="dialog" aria-modal="true" onClick={onClose}>
      <div className="modal-content" onClick={stop}>
        <button className="close-btn" onClick={onClose} aria-label="닫기">&times;</button>
        {loading && <p>로딩 중...</p>}
        {error && <p className="error">{error}</p>}
        {user && (
          <div className="detail">
            <img
              className="avatar-large"
              src={user.photoUrl || process.env.PUBLIC_URL + '/default-avatar.png'}
              alt={user.name}
              onError={e => {
                e.currentTarget.onerror = null;
                e.currentTarget.src = process.env.PUBLIC_URL + '/default-avatar.png';
              }}
            />
            {(() => {
              const info = splitBracketName(user.name || user.userName);

              if (!info.hasTag) {
                return <h2>{user.name || user.userName}</h2>;
              }

              return (
                <h2 className="modal-name">
                  <TagLabel tagInfo={info} className="modal-name__tag" />
                  {info.text && <span className="modal-name__text">{info.text}</span>}
                </h2>
              );
            })()}
            <p><strong>소속:</strong> {user.departmentName}</p>
            <p><strong>직책:</strong> {user.positionName}</p>
            <p><strong>이메일:</strong> {user.email || '–'}</p>
            <p><strong>내선번호:</strong> {user.directTel || '–'}</p>
            <p><strong>휴대전화:</strong> {user.mobileNo || '–'}</p>
            <p><strong>입사일:</strong> {user.joinDate || '–'}</p>
          </div>
        )}
      </div>
    </div>,
    document.body
  );
}
