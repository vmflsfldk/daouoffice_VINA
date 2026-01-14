import React, { useState } from 'react';
import DeptTree from './DeptTree';
import UserList from './UserList';
import SearchBar from './SearchBar';

export default function OrgPage() {
  const [selectedDept, setSelectedDept] = useState(null);
  const [treeData, setTreeData] = useState([]);
  const [searchResults, setSearchResults] = useState(null);
  const [selectedUser, setSelectedUser] = useState(null);

  const scrollToTop = () => {
    if (typeof window !== 'undefined') {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  // 트리에서 특정 부서 정보를 찾는 함수
  const findDeptInTree = (nodes, targetId) => {
    for (const node of nodes) {
      if (node.id === targetId) {
        return node;
      }
      if (node.children && node.children.length > 0) {
        const found = findDeptInTree(node.children, targetId);
        if (found) return found;
      }
    }
    return null;
  };

  const handleUserSelect = (user) => {
    // 검색 결과에서 사용자를 클릭한 경우 해당 부서로 이동
    const deptFromTree = findDeptInTree(treeData, user.departmentId);

    const dept = deptFromTree ? {
      id: deptFromTree.id,
      name: deptFromTree.name,
      parentId: deptFromTree.parentId,
      memberCount: deptFromTree.memberCount,
      children: deptFromTree.children
    } : {
      id: user.departmentId,
      name: user.departmentName,
      memberCount: 0 // 트리에서 찾을 수 없는 경우 fallback
    };

    setSelectedDept(dept);
    setSearchResults(null);
    setSelectedUser(null);
    scrollToTop();
  };

  const handleTreeDataLoad = (data) => {
    setTreeData(data);
  };

  const handleDeptSelect = (dept) => {
    setSelectedDept(dept);
    setSearchResults(null);
    setSelectedUser(null);
    scrollToTop();
  };

  const handleSearchResults = (results) => {
    setSearchResults(results);
    setSelectedDept(null);
    setSelectedUser(null);
    if (Array.isArray(results)) {
      scrollToTop();
    }
  };

  return (
    <div className="org-page">
      <aside className="sidebar">
        <h1>조직도</h1>
        <SearchBar
          onUserSelect={handleUserSelect}
          onSearchResults={handleSearchResults}
        />
        <DeptTree
          onSelect={handleDeptSelect}
          selected={selectedDept?.id}
          onTreeDataLoad={handleTreeDataLoad}
        />
      </aside>
      <main className="content">
        <UserList
          dept={selectedDept}
          searchResults={searchResults}
          selectedUser={selectedUser}
        />
      </main>
    </div>
  );
}