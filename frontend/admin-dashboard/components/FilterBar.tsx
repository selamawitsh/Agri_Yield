'use client';

interface FilterBarProps {
  roleFilter: string;
  statusFilter: string;
  onRoleChange: (role: string) => void;
  onStatusChange: (status: string) => void;
  onRefresh: () => void;
}

export default function FilterBar({
  roleFilter,
  statusFilter,
  onRoleChange,
  onStatusChange,
  onRefresh,
}: FilterBarProps) {
  const roles = ['ALL', 'FARMER', 'INVESTOR', 'MERCHANT', 'OFF_TAKER', 'ADMIN'];
  const statuses = ['ALL', 'ACTIVE', 'SUSPENDED', 'PENDING_VERIFICATION'];

  return (
    <div className="bg-white rounded-lg shadow-md p-4 mb-6">
      <div className="flex flex-wrap gap-4 items-center justify-between">
        <div className="flex gap-4">
          <div>
            <label className="block text-sm text-gray-600 mb-1">Role</label>
            <select
              value={roleFilter}
              onChange={(e) => onRoleChange(e.target.value)}
              className="border rounded-lg px-3 py-2"
            >
              {roles.map((role) => (
                <option key={role} value={role}>{role}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm text-gray-600 mb-1">Status</label>
            <select
              value={statusFilter}
              onChange={(e) => onStatusChange(e.target.value)}
              className="border rounded-lg px-3 py-2"
            >
              {statuses.map((status) => (
                <option key={status} value={status}>{status}</option>
              ))}
            </select>
          </div>
        </div>
        <button onClick={onRefresh} className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700">
          Refresh
        </button>
      </div>
    </div>
  );
}
