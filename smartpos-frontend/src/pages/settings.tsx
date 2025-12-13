"use client";

import React from "react";
import { Layout, Header, Content } from "@/components/layout/Layout";
import { Card, Button, Input } from "@/components/common/FormElements";
import { FiSave } from "react-icons/fi";
import toast from "react-hot-toast";

export default function Settings() {
  const [settings, setSettings] = React.useState({
    storeName: "SmartPOS Store",
    storeEmail: "store@example.com",
    storePhone: "+1 (555) 000-0000",
    currency: "USD",
    taxRate: 10,
  });

  const handleSave = () => {
    localStorage.setItem("smartpos-settings", JSON.stringify(settings));
    toast.success("Settings saved successfully");
  };

  const handleChange = (field: string, value: any) => {
    setSettings({
      ...settings,
      [field]: value,
    });
  };

  return (
    <Layout>
      <Header title="Settings" subtitle="Configure your store settings" />
      <Content>
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card title="Store Information">
            <div className="space-y-4">
              <Input
                label="Store Name"
                value={settings.storeName}
                onChange={(e) => handleChange("storeName", e.target.value)}
              />
              <Input
                label="Store Email"
                type="email"
                value={settings.storeEmail}
                onChange={(e) => handleChange("storeEmail", e.target.value)}
              />
              <Input
                label="Store Phone"
                value={settings.storePhone}
                onChange={(e) => handleChange("storePhone", e.target.value)}
              />
            </div>
          </Card>

          <Card title="Business Settings">
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Currency
                </label>
                <select
                  value={settings.currency}
                  onChange={(e) => handleChange("currency", e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="USD">USD ($)</option>
                  <option value="EUR">EUR (€)</option>
                  <option value="GBP">GBP (£)</option>
                  <option value="JPY">JPY (¥)</option>
                </select>
              </div>
              <Input
                label="Default Tax Rate (%)"
                type="number"
                step="0.01"
                value={settings.taxRate}
                onChange={(e) => handleChange("taxRate", parseFloat(e.target.value))}
              />
            </div>
          </Card>
        </div>

        <Card className="mt-6">
          <div className="flex justify-end">
            <Button onClick={handleSave} variant="primary">
              <FiSave className="inline mr-2" /> Save Settings
            </Button>
          </div>
        </Card>
      </Content>
    </Layout>
  );
}
