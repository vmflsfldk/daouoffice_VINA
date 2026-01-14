// src/components/UserList.jsx
import React, { useEffect, useState, useRef, useMemo } from 'react';
import axios from 'axios';
import UserDetailModal from './UserDetailModal';
import './UserList.css';
import TagLabel from './TagLabel';
import { splitBracketName } from '../utils/nameFormat';
import { sortDepartments } from '../utils/deptOrder';

export default function UserList({ dept, searchResults, selectedUser }) {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');
  const prevDeptIdRef = useRef(null);
  const [showDescendants, setShowDescendants] = useState(false);

  // 모달 관련 상태
  const [modalUser, setModalUser] = useState(null);
  const [modalLoading, setModalLoading] = useState(false);
  const [modalError, setModalError] = useState('');

  useEffect(() => {
    // 선택된 단일 사용자가 있는 경우 해당 사용자만 표시
    if (selectedUser) {
      const singleUser = [{
        userId: selectedUser.userId,
        userName: selectedUser.userName,
        positionName: selectedUser.positionName,
        photoUrl: selectedUser.photoUrl,
        sortOrder: 0,
        type: selectedUser.type ?? 1,
        departmentId: selectedUser.departmentId ?? dept?.id ?? null,
        departmentName: selectedUser.departmentName ?? dept?.name ?? ''
      }];
      setUsers(singleUser);
      setError('');
      return;
    }

    // 검색 결과가 있으면 검색 결과를 표시
    if (searchResults) {
      // 검색 결과를 UserDto 형태로 변환
      const searchUsers = searchResults.map(user => ({
        userId: user.userId,
        userName: user.userName,
        positionName: user.positionName,
        photoUrl: user.photoUrl,
        sortOrder: 0, // 검색 결과는 sortOrder가 없으므로 0으로 설정
        type: user.type ?? 1,
        departmentId: user.departmentId ?? null,
        departmentName: user.departmentName ?? ''
      }));
      setUsers(searchUsers);
      setError('');
      return;
    }

    // 검색 결과가 없고 부서가 선택되지 않았으면 빈 화면
    if (!dept || Number(dept.id) === 0) {
      setUsers([]);
      return;
    }

    // 부서 ID가 실제로 변경된 경우에만 API 호출
    if (dept.id !== prevDeptIdRef.current) {
      prevDeptIdRef.current = dept.id;

      setError('');
      axios
        .get(`/api/org/departments/${dept.id}/users`)
        .then(res => {
          setUsers(res.data);
        })
        .catch(err => {
          setError('구성원 정보를 불러오는 중 오류가 발생했습니다.');
          setUsers([]);
        });
    }
  }, [dept, searchResults, selectedUser]);

  const groupedSections = useMemo(() => {
    if (!dept || !dept.id || !Array.isArray(users) || users.length === 0) {
      return { flat: [], nested: [] };
    }

    const baseId = Number(dept.id);
    if (Number.isNaN(baseId)) {
      return { flat: [], nested: [] };
    }

    const flatSections = [];
    const childMap = new Map();
    const childOrder = new Map();

    const orderedChildren = sortDepartments(dept.children ?? [], dept.name);

    orderedChildren.forEach((child, index) => {
      const childId = Number(child.id);
      if (!Number.isNaN(childId)) {
        childMap.set(childId, child);
        childOrder.set(childId, index);
      }
    });

    const directMembers = [];
    const grouped = new Map();

    users.forEach((user) => {
      const rawDeptId = user.departmentId ?? baseId;
      const userDeptId = rawDeptId === null ? baseId : Number(rawDeptId);

      if (Number.isNaN(userDeptId) || userDeptId === baseId) {
        directMembers.push(user);
        return;
      }

      if (!grouped.has(userDeptId)) {
        grouped.set(userDeptId, []);
      }
      grouped.get(userDeptId).push(user);
    });

    const directSection = directMembers.length > 0
      ? {
          key: `direct-${baseId}`,
          title: dept.name,
          subtitle: `(${directMembers.length})`,
          users: directMembers,
          variant: 'direct',
          descendants: []
        }
      : null;

    if (directSection) {
      flatSections.push(directSection);
    }

    const sortedGroups = Array.from(grouped.entries()).sort((a, b) => {
      const [deptA] = a;
      const [deptB] = b;
      const orderA = childOrder.has(deptA) ? childOrder.get(deptA) : Number.MAX_SAFE_INTEGER;
      const orderB = childOrder.has(deptB) ? childOrder.get(deptB) : Number.MAX_SAFE_INTEGER;

      if (orderA !== orderB) {
        return orderA - orderB;
      }

      const nameA = childMap.get(deptA)?.name ?? a[1][0]?.departmentName ?? '';
      const nameB = childMap.get(deptB)?.name ?? b[1][0]?.departmentName ?? '';
      return nameA.localeCompare(nameB, 'ko');
    });

    sortedGroups.forEach(([deptId, memberList]) => {
      const childInfo = childMap.get(deptId);
      const sectionTitle = childInfo?.name ?? memberList[0]?.departmentName ?? '미지정 부서';
      flatSections.push({
        key: `dept-${deptId}`,
        title: sectionTitle,
        subtitle: `(${memberList.length})`,
        users: memberList,
        variant: 'child',
        descendants: []
      });
    });

    const processed = new Set();

    const buildNestedSection = (department) => {
      const departmentId = Number(department.id);
      if (Number.isNaN(departmentId)) {
        return null;
      }

      const memberList = grouped.get(departmentId) ?? [];
      const childDepartments = sortDepartments(department.children ?? [], department.name);
      const descendantSections = childDepartments
        .map((childDept) => buildNestedSection(childDept))
        .filter(Boolean);

      if (memberList.length === 0 && descendantSections.length === 0) {
        return null;
      }

      processed.add(departmentId);

      return {
        key: `dept-${departmentId}`,
        title: department.name ?? '미지정 부서',
        subtitle: `(${memberList.length})`,
        users: memberList,
        variant: 'child',
        descendants: descendantSections
      };
    };

    const nestedSections = [];

    if (directSection) {
      nestedSections.push(directSection);
    }

    const nestedChildren = orderedChildren
      .map((childDept) => buildNestedSection(childDept))
      .filter(Boolean);

    nestedSections.push(...nestedChildren);

    const fallbackSections = Array.from(grouped.entries())
      .filter(([deptId]) => !processed.has(deptId))
      .map(([deptId, memberList]) => ({
        key: `dept-${deptId}`,
        title: memberList[0]?.departmentName ?? '미지정 부서',
        subtitle: `(${memberList.length})`,
        users: memberList,
        variant: 'child',
        descendants: []
      }))
      .sort((a, b) => a.title.localeCompare(b.title, 'ko'));

    nestedSections.push(...fallbackSections);

    return { flat: flatSections, nested: nestedSections };
  }, [dept, users]);

  // 사용자 카드 클릭 시 상세 정보 모달 열기
  const handleUserClick = async (user) => {
    const fallbackUser = user
      ? {
          ...user,
          name: user.name ?? user.userName,
          userName: user.userName ?? user.name ?? '',
          departmentName: user.departmentName ?? dept?.name ?? '',
          positionName: user.positionName ?? '직책 미지정',
          email: user.email ?? user.emailAddr ?? user.mailAddr ?? '',
          directTel: user.directTel ?? user.tel ?? '',
          mobileNo: user.mobileNo ?? user.mobile ?? '',
          joinDate: user.joinDate ?? user.entryDate ?? user.hireDate ?? '',
          photoUrl: user.photoUrl ?? user.profileImageUrl ?? ''
        }
      : null;

    setModalLoading(true);
    setModalError('');
    setModalUser(fallbackUser);

    if (!user?.userId) {
      setModalError('선택한 사용자 정보가 없습니다.');
      setModalLoading(false);
      return;
    }

    try {
      const departmentId = user.departmentId ?? dept?.id;
      const requestOptions = {};

      if (departmentId) {
        requestOptions.params = { departmentId };
      }

      const response = await axios.get(`/api/org/users/${user.userId}`, requestOptions);
      setModalUser({ ...fallbackUser, ...response.data });
    } catch (error) {
      // 상세 조회가 실패해도 기존 목록에서 가져온 정보로 모달을 표시합니다.
      setModalError('사용자 정보를 불러오는 중 오류가 발생했습니다. 기본 정보를 표시합니다.');
    } finally {
      setModalLoading(false);
    }
  };

  // 모달 닫기
  const handleCloseModal = () => {
    setModalUser(null);
    setModalLoading(false);
    setModalError('');
  };

  // 사용자 카드 컴포넌트
  const UserCard = ({ user }) => {
    const handleClick = (e) => {
      e.preventDefault();
      e.stopPropagation();
      handleUserClick(user);
    };

    return (
      <button type="button" className="user-card" onClick={handleClick}>
        {user.photoUrl ? (
          <img
            className="user-card__avatar"
            src={user.photoUrl}
            alt={user.userName}
            onError={e => (e.currentTarget.src = '/default-avatar.png')}
            loading="lazy"
          />
        ) : (
          <div className="user-card__avatar user-card__avatar--placeholder" aria-hidden="true" />
        )}
        <div className="user-card__info">
          <div className="user-card__name">{renderDisplayName(user.userName)}</div>
          <div className="user-card__position">{user.positionName || '직책 미지정'}</div>
        </div>
      </button>
    );
  };

  const renderModal = () => (
    (modalUser || modalLoading || modalError) && (
      <UserDetailModal
        user={modalUser}
        loading={modalLoading}
        error={modalError}
        onClose={handleCloseModal}
      />
    )
  );

  const renderDisplayName = (name) => {
    const info = splitBracketName(name);

    if (!info.hasTag) {
      return <span>{name}</span>;
    }

    return (
      <>
        <TagLabel tagInfo={info} className="user-card__name-tag" />
        {info.text && <span className="user-card__name-main">{info.text}</span>}
      </>
    );
  };

  const renderTitle = (title) => {
    const info = splitBracketName(title);

    if (!info.hasTag) {
      return title;
    }

    return (
      <span className="user-list-title user-list-title--stacked">
        <TagLabel tagInfo={info} className="user-list-title__tag" />
        {info.text && <span className="user-list-title__text">{info.text}</span>}
      </span>
    );
  };

  const renderListSection = (title, subtitle, list = users) => (
    <>
      <div className="user-list-container">
        <header className="user-list-header">
          <h2>
            {renderTitle(title)}
            {subtitle && <span>{subtitle}</span>}
          </h2>
          <p className="user-list-subtitle">구성원을 클릭하면 상세 정보를 확인할 수 있습니다.</p>
        </header>

        {error && <div className="error-message">{error}</div>}

        <div className="user-grid">
          {list.map(u => <UserCard key={u.userId} user={u} />)}
        </div>
      </div>

      {renderModal()}
    </>
  );

  const renderSectionTree = (section, depth = 0) => {
    const hasDescendants = Array.isArray(section.descendants) && section.descendants.length > 0;

    return (
      <section
        key={section.key}
        className={`user-list-section user-list-section--${section.variant} ${
          depth > 0 ? 'user-list-section--nested' : ''
        }`.trim()}
      >
        <header className="user-list-header">
          <h2>
            {renderTitle(section.title)}
            {section.subtitle && <span>{section.subtitle}</span>}
          </h2>
          <p className="user-list-subtitle">구성원을 클릭하면 상세 정보를 확인할 수 있습니다.</p>
        </header>

        {section.users.length > 0 && (
          <div className="user-grid">
            {section.users.map(user => (
              <UserCard key={user.userId} user={user} />
            ))}
          </div>
        )}

        {showDescendants && hasDescendants && (
          <div className="user-list-subsections">
            {section.descendants.map(descendant => (
              <div key={descendant.key} className="user-list-subsection">
                {renderSectionTree(descendant, depth + 1)}
              </div>
            ))}
          </div>
        )}
      </section>
    );
  };

  const renderGroupedDepartments = () => {
    const hasSections = groupedSections.flat.length > 0;

    if (error && !hasSections) {
      return (
        <>
          <div className="error-message">{error}</div>
          {renderModal()}
        </>
      );
    }

    if (!hasSections) {
      return (
        <>
          <div className="user-placeholder">선택된 부서에 속한 구성원이 없습니다.</div>
          {renderModal()}
        </>
      );
    }

    const sectionsToRender = showDescendants ? groupedSections.nested : groupedSections.flat;

    return (
      <>
        <div className="user-list-controls">
          <button
            type="button"
            className={`user-list-toggle${showDescendants ? ' is-active' : ''}`}
            onClick={() => setShowDescendants(prev => !prev)}
          >
            {showDescendants ? '하위부서 숨기기' : '하위부서 보기'}
          </button>
        </div>

        <div className="user-list-groups">
          {error && <div className="error-message">{error}</div>}

          {sectionsToRender.map(section =>
            showDescendants ? renderSectionTree(section) : (
              <section
                key={section.key}
                className={`user-list-section user-list-section--${section.variant}`}
              >
                <header className="user-list-header">
                  <h2>
                    {renderTitle(section.title)}
                    {section.subtitle && <span>{section.subtitle}</span>}
                  </h2>
                  <p className="user-list-subtitle">구성원을 클릭하면 상세 정보를 확인할 수 있습니다.</p>
                </header>

                <div className="user-grid">
                  {section.users.map(user => (
                    <UserCard key={user.userId} user={user} />
                  ))}
                </div>
              </section>
            )
          )}
        </div>

        {renderModal()}
      </>
    );
  };

  // 선택된 단일 사용자가 있는 경우
  if (selectedUser) {
    const singleUserList = [{
      userId: selectedUser.userId,
      userName: selectedUser.userName,
      positionName: selectedUser.positionName,
      photoUrl: selectedUser.photoUrl,
      sortOrder: 0,
      type: selectedUser.type ?? 1,
      departmentId: selectedUser.departmentId ?? dept?.id ?? null,
      departmentName: selectedUser.departmentName ?? dept?.name ?? ''
    }];

    return renderListSection(
      `${selectedUser.userName}`,
      selectedUser.departmentName ? `(${selectedUser.departmentName})` : '',
      singleUserList
    );
  }

  // 검색 결과가 있는 경우
  if (searchResults) {
    const searchUsers = searchResults.map(user => ({
      userId: user.userId,
      userName: user.userName,
      positionName: user.positionName,
      photoUrl: user.photoUrl,
      sortOrder: 0,
      type: user.type ?? 1,
      departmentId: user.departmentId ?? null,
      departmentName: user.departmentName ?? ''
    }));

    return renderListSection('검색 결과', `(${searchResults.length})`, searchUsers);
  }

  // 부서가 선택되지 않은 경우
  if (!dept || Number(dept.id) === 0) {
    return <div className="user-placeholder">부서를 선택하거나 검색해주세요.</div>;
  }

  // 부서가 선택된 경우
  return renderGroupedDepartments();
}
