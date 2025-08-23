import React, { useState, useEffect } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "./ui/dialog";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { Label } from "./ui/label";
import { Badge } from "./ui/badge";
import { X } from "lucide-react";
import { Restaurant } from "./RestaurantCard";

interface EditRestaurantDialogProps {
  restaurant: Restaurant;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onEditRestaurant: (restaurant: Omit<Restaurant, "rating" | "favoriteCount">) => void;
}

export function EditRestaurantDialog({ 
  restaurant, 
  open, 
  onOpenChange, 
  onEditRestaurant 
}: EditRestaurantDialogProps) {
  const [formData, setFormData] = useState({
    id: restaurant.id,
    name: restaurant.name,
    cuisine: restaurant.cuisine,
    description: restaurant.description,
    tags: restaurant.tags,
    image: restaurant.image || ""
  });
  const [currentTag, setCurrentTag] = useState("");

  // Reset form data when restaurant changes or dialog opens
  useEffect(() => {
    if (open && restaurant) {
      setFormData({
        id: restaurant.id,
        name: restaurant.name,
        cuisine: restaurant.cuisine,
        description: restaurant.description,
        tags: restaurant.tags,
        image: restaurant.image || ""
      });
    }
  }, [restaurant, open]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.name && formData.cuisine) {
      onEditRestaurant(formData);
    }
  };

  const addTag = () => {
    if (currentTag && !formData.tags.includes(currentTag)) {
      setFormData(prev => ({ ...prev, tags: [...prev.tags, currentTag] }));
      setCurrentTag("");
    }
  };

  const removeTag = (tag: string) => {
    setFormData(prev => ({ ...prev, tags: prev.tags.filter(t => t !== tag) }));
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Edit Restaurant</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <Label htmlFor="restaurant-name">Restaurant Name *</Label>
            <Input
                id="restaurant-name"
                value={formData.name}
                onChange={(e) => setFormData(prev => ({...prev, name: e.target.value}))}
                required
            />
          </div>

          <div>
            <Label htmlFor="cuisine">Cuisine Type *</Label>
            <Input
                id="cuisine"
                value={formData.cuisine}
                onChange={(e) => setFormData(prev => ({...prev, cuisine: e.target.value}))}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    e.preventDefault();
                  }
                }}
                required
            />
          </div>
          
          <div>
            <Label>Tags</Label>
            <div className="flex space-x-2 mb-2">
              <Input
                placeholder="Add tag"
                value={currentTag}
                onChange={(e) => setCurrentTag(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), addTag())}
              />
              <Button type="button" onClick={addTag} size="sm">Add</Button>
            </div>
            <div className="flex flex-wrap gap-1">
              {formData.tags.map((tag) => (
                <Badge key={tag} variant="secondary" className="gap-1">
                  {tag}
                  <X className="h-3 w-3 cursor-pointer" onClick={() => removeTag(tag)} />
                </Badge>
              ))}
            </div>
          </div>
          
          <div>
            <Label htmlFor="image">Image URL</Label>
            <Input
              id="image"
              value={formData.image}
              onChange={(e) => setFormData(prev => ({ ...prev, image: e.target.value }))}
              placeholder="Optional - will use default if empty"
            />
          </div>
          
          <div className="flex justify-end space-x-2">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit">Save Changes</Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}